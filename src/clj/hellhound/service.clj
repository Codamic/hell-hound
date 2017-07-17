(ns hellhound.service
  "In order to create `service` maps to use with your hellhound
  application, you have to use `create-service-map` function in
  this namespace."
  (:require
   [io.pedestal.http :as http]
   [hellhound.core  :as hellhound]))


(def default-service-map
  {:env                  (hellhound/env)
   ::http/resource-path  (hellhound/get-config :public-files-path)
   ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}
   ::http/type           :immutant
   ::http/host           (hellhound/get-config :http-host)
   ::http/port           (hellhound/get-config :http-port)

   ;; TODO: Add immutant related features in the
   ;; map blow
   ::http/container-options {:h2c? true
                             :h2? false}})

(defn remove-unnecessary-interpretors
  [service-map]
  (let [interceptors (::http/interceptors service-map)]
    (assoc service-map ::http/interceptors (rest interceptors))))

(defn create-service-map
  "Creates a system map and fills some default values for the map"
  [service-map]
  (-> (merge default-service-map service-map)
      (http/default-interceptors)
      (remove-unnecessary-interpretors)))

(defmacro defservice-map
  "A short cut for creating service maps using `create-service-map`
  function."
  [map-name service-map]
  `(def ~map-name (hellhound.service/create-service-map ~service-map)))
