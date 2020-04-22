(ns tawny.bubo.cli
  (:require
   [cli-matic.core :refer [run-cmd]]
   [cli-matic.utils]
   [clojure.main]
   [clojure.string]
   [say-cheez.core]
   [tawny.bubo.core :as c])
  (:gen-class))

(defn- stem [filename]
  (first
   (clojure.string/split
    (last
     (clojure.string/split filename #"[/]"))
    #"[.]")))


(defn blitz-clojure-core [ns]
  (doseq
      [[k v] (ns-map ns)
       :when (= (find-ns 'clojure.core) (:ns (meta v)))]
    (ns-unmap ns k)))



(defn script [{:keys [script stdout]}]
  (binding [c/*opt-to-std-out* stdout]
    (clojure.main/with-bindings
      (in-ns 'user)
      (require '[clojure.core :as cc])
      (blitz-clojure-core *ns*)
      (use 'tawny.bubo.core)
      (use 'tawny.bubo.util)
      (use 'tawny.owl)
      (use 'tawny.english)
      (clojure.main/load-script script))))

(def BUILD (say-cheez.core/current-build-env))

(def CONFIGURATION
  {:app         {:command     "bubo"
                 :description "A command-line OWL ontology tool"
                 :version     (-> BUILD :project :version)
                 }

   :commands [
              {:command     "script"
               :description "Run a bubo script"
               :opts        [{:option "script" :short 0 :as "Script Help" :type :string}
                             {:option "stdout" :as "Stdout Help" :type :with-flag}]
               :runs        script
               }
              ]
   })

(defn -main[& args]
  (run-cmd
   (if (or
        ;; No args should give help
        (= nil args)
        ;; First arg is argument interpret that
        (= \- (first (first args)))
        ;; First arg is a subcommand, should interpret that
        ((cli-matic.utils/all-subcommands CONFIGURATION) (first args)))
     args
     ;; First arg is something unclear, so it's probably script file name
     (cons "script" args))
   CONFIGURATION))
