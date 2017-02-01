(set-env!
 :resource-paths #{"src/clj"
                   "src/cljs"
                   "src/cljc"}
 :checkouts '[;[cljsjs/grommet                "1.1.0-0"]
              [codamic/sente                 "1.11.1"]]

 :dependencies '[[org.clojure/clojure        "1.9.0-alpha14"]
                 [org.clojure/clojurescript  "RELEASE"]
                 [cljsjs/jquery              "2.2.4-0"]
                 [bidi                       "2.0.14"]
                 [reagent                    "0.6.0"]
                 [ring                       "1.5.1"]
                 [ring/ring-defaults         "0.3.0-beta1"]
                 [re-frame                   "0.8.0"]
                 [secretary                  "1.2.3"]
                 [com.stuartsierra/component "0.3.1"]
                 [com.taoensso/tempura       "1.0.0-RC3"]
                 [codamic/sente              "1.11.1"]
                 [com.taoensso/timbre        "4.7.4"]
                 [org.danielsz/system        "0.3.2-SNAPSHOT"]
                 [environ                    "1.1.0"]
                 [boot-environ               "1.1.0"]
                 [com.cemerick/friend        "0.2.3"]
                 [ring-logger                "0.7.6"]
                 [org.immutant/immutant      "2.1.5"
                  :exclusions [ch.qos.logback/logback-classic]]
                 [com.fzakaria/slf4j-timbre  "0.3.2"]
                 [potemkin                   "0.4.3"]
                 [com.cognitect/transit-clj  "RELEASE"]
                 [com.cognitect/transit-cljs "RELEASE"]
                 [colorize                   "0.1.1"
                  :exclusions [org.clojure/clojure]]
                 [ring/ring-anti-forgery     "1.1.0-beta1"]

                 ;; Cljs repl dependencies ----------------------------
                 [adzerk/boot-cljs-repl      "0.3.3"          :scope "test"]
                 [com.cemerick/piggieback    "0.2.1"          :scope "test"]
                 [weasel                     "0.7.0"          :scope "test"]
                 [org.clojure/tools.nrepl    "0.2.12"         :scope "test"]
                 ;; ---------------------------------------------------
                 [funcool/codeina            "0.5.0"          :scope "test"]
                 [codamic/boot-codeina       "0.2.0-SNAPSHOT" :scope "test"]])


(require '[funcool.boot-codeina :refer [apidoc]]
         '[taoensso.sente])

(def VERSION       "0.12.0-SNAPSHOT")
(def DESCRIPTION   "A simple full-stack web framework for clojure")


(task-options!
 pom {:project     'codamic/hellhound
      :version     VERSION
      :description DESCRIPTION
      :license     {"GPLv3"
                    "https://www.gnu.org/licenses/gpl.html"}
      :url         "http://github.com/Codamic/hellhound"
      :scm         {:url "https://github.com/Codamic/hellhound"}}

 jar {:manifest    {"Description" DESCRIPTION
                    "Url"         "http://github.com/Codamic/hellhound"}}

 apidoc            {:version     VERSION
                    :title       "HellHound"
                    :sources     #{"src"}
                    :reader      :clojure
                    :target      "doc/api/clj"
                    :description DESCRIPTION}
 push              {:repo "clojars"})


(deftask cljs-docs
  "Create the documents for cljs parts."
  []
  (apidoc :reader :clojurescript :target "doc/api/cljs" :source #{"src/cljs" "src/cljc"}))

(deftask clj-docs
  "Create the documents for clj parts."
  []
  (apidoc :source #{"src/clj" "src/cljc"}))

(deftask docs
  "Create all the docs."
  []
  (comp
   (cljs-docs)
   (clj-docs)))

(deftask build
  "Build and install the hellhound"
  []
  (comp (pom) (jar) (install)))

(deftask release
  "Build and release the snapshot version of hellhound"
  []
  (comp (pom) (jar) (push)))

;; (deftask release-snapshot
;;   "Build and release the snapshot version of hellhound"
;;   []
;;   (comp (pom) (jar) (push-snapshot)))
