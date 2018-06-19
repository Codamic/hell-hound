(ns hellhound.system.impl.splitter
  {:added 1.0}
  (:require
   [hellhound.system.operations :as op]
   [hellhound.async :as ha]
   [hellhound.streams :as streams]
   [hellhound.system.protocols :as proto]
   [hellhound.utils :refer [todo]]))


(defn- transform-and-put
  "Applies all the operations defined in the given `ops` map and returns
  the transformed value."
  [value {:keys [filter-fn map-fn] :as ops}]
  (if (filter-fn value)
    (map-fn value)
    nil))


(defn- connect
  [source sink op-map]
  (streams/connect-via source
                       (fn [v]
                         (when-let [tv (transform-and-put v op-map)]
                           (streams/put! sink tv)))
                       sink))


(deftype OutputSplitter [source sinks]
  proto/Splitter
  (connect
    [this sink operation-map]
    ;; Simply puts the sink and it's operation-map into sinks vector
    (let [m (or operation-map op/default-operations)]
      (swap! sinks conj [sink m])))

  (commit
    [this]
    (doseq [[sink op-map] @sinks]
      (connect source sink op-map))
    this))


(defn output-splitter
  "Create and return an output-splitter for the given `source` channel."
  ([source]
   (output-splitter source :single-thread))
  ([source execution-mode]
   (cond
     (= execution-mode :single-thread) (OutputSplitter. source (atom []))
     (= execution-mode :multi-thread) (ThreadedOutputSplitter. source (atom []) (threadpool 4))
     :else (throw (ex-info (format "Don't know about '%s' execution mode."
                                   execution-mode)
                           :cause {:execution-mode execution-mode})))))


(comment
  (let [a (streams/stream 10)
        b (streams/stream 10)
        c (streams/stream 10)
        tp (threadpool 4)
        splitter (output-splitter a :single-thread)
        read (fn [x y]
               (streams/consume
                (fn [v]
                  (ha/future-with tp
                                  (Thread/sleep 100)
                                  (println (format "%s-%s: %s" (.getName (Thread/currentThread)) y v)))) x))]

    (proto/connect splitter b op/default-operations)
    (proto/connect splitter c op/default-operations)
    (proto/commit splitter)

    (doseq [x [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24]]
      (streams/put! a x))
    (read b "b")
    (read c "c")

    (streams/close! a)
    (streams/close! b)
    (streams/close! c)))
