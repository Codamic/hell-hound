(ns hellhound.logger
  (:require
   [taoensso.timbre              :as timbre]
   [hellhound.logger.formatters  :as formatter]
   [hellhound.logger.middlewares :as middlewares]))


(defn- fline [and-form] (:line (meta and-form)))

(defmacro log*
  [config level & args]
  `(taoensso.timbre/log! ~level  :p ~args
                         ~{:?line (fline &form) :config config}))
(defmacro log
  [level & args]
  `(taoensso.timbre/log! ~level  :p ~args
                         ~{:?line (fline &form)}))
(defmacro trace
  [& args]
  `(taoensso.timbre/log! :trace  :p ~args
                         ~{:?line (fline &form)}))


(defmacro debug
  [& args]
  `(taoensso.timbre/log! :debug  :p ~args
                         ~{:?line (fline &form)}))
(defmacro info
  [& args]
  `(taoensso.timbre/log! :info   :p ~args
                         ~{:?line (fline &form)}))
(defmacro warn
  [& args]
  `(taoensso.timbre/log! :warn   :p ~args
                         ~{:?line (fline &form)}))
(defmacro error
  [& args]
  `(taoensso.timbre/log! :error  :p ~args
                         ~{:?line (fline &form)}))
(defmacro exception
  [& args]
  `(taoensso.timbre/log! :exception  :p ~args
                         ~{:?line (fline &form)}))
(defmacro fatal
  [& args]
  `(taoensso.timbre/log! :fatal  :p ~args
                         ~{:?line (fline &form)}))
(defmacro report
  [& args]
  `(taoensso.timbre/log! :report :p ~args
                         ~{:?line (fline &form)}))

;;; Log using format-style args
(defmacro logf*
  [config level & args]
  `(taoensso.timbre/log! ~level  :f ~args
                         ~{:?line (fline &form) :config config}))
(defmacro logf
  [level & args]
  `(taoensso.timbre/log! ~level  :f ~args
                         ~{:?line (fline &form)}))
(defmacro tracef
  [& args]
  `(taoensso.timbre/log! :trace  :f ~args
                         ~{:?line (fline &form)}))
(defmacro debugf
  [& args]
  `(taoensso.timbre/log! :debug  :f ~args
                         ~{:?line (fline &form)}))
(defmacro infof
  [& args]
  `(taoensso.timbre/log! :info   :f ~args
                         ~{:?line (fline &form)}))
(defmacro warnf
  [& args]
  `(taoensso.timbre/log! :warn   :f ~args
                         ~{:?line (fline &form)}))
(defmacro errorf
  [& args]
  `(taoensso.timbre/log! :error  :f ~args
                         ~{:?line (fline &form)}))
(defmacro fatalf
  [& args]
  `(taoensso.timbre/log! :fatal  :f ~args
                         ~{:?line (fline &form)}))
(defmacro reportf
  [& args]
  `(taoensso.timbre/log! :report :f ~args
                         ~{:?line (fline &form)}))


(def default-config
  {:level :info
   :enabled? true
   :output-fn (formatter/default-dev-formatter {:important-namespaces ["hellhound.*"]})
   :middleware [middlewares/exceptions]
   :ns-blacklist ["io.pedestal.*"]
   :filter-stracktrace true
   :appenders
   {:debug-appender {:enabled? true :min-level :debug :output-fn  :inherit
                     :fn (fn [data]
                           (let [{:keys [output_]} data
                                 formatted-output-str (force output_)]
                             (println formatted-output-str)))}}})


(defn init!
  [config]
  (let [final-config (merge default-config config)]
    (timbre/set-config! final-config)))
