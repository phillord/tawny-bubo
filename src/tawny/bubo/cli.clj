(ns tawny.bubo.cli
  (:require
   [cli-matic.core :refer [run-cmd]]
   [cli-matic.utils]
   [clojure.main]
   [clojure.string]
   [say-cheez.core]
   [tawny.bubo.core])
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



(defn script [{:keys [script]}]
  (clojure.main/with-bindings
    (in-ns 'user)
    (blitz-clojure-core *ns*)
    (require '[clojure.core :as cc])
    (use 'tawny.bubo.core)
    (use 'tawny.bubo.util)
    (use 'tawny.owl)
    (use 'tawny.english)
    (clojure.main/load-script script)))

(defn add_numbers
  "Sums A and B together, and prints it in base `base`"
  [{:keys [a1 a2 base]}]
  (println
   (Integer/toString (+ a1 a2) base)))

(def BUILD (say-cheez.core/current-build-env))

(def CONFIGURATION
  {:app         {:command     "bubo"
                 :description "A command-line OWL ontology tool"
                 :version     (-> BUILD :project :version)
                 }

   :commands [
              {:command     "script"
               :description "Run a bubo script"
               :opts        [{:option "script" :short 0 :as "Script Help" :type :string}]
               :runs        script
               }
              ]
   })

(defn -main[& args]
  (if (and (first args)
           (or
            (= \- (first (first args)))
            ((cli-matic.utils/all-subcommands CONFIGURATION) (first args))))
    (run-cmd args CONFIGURATION)
    (run-cmd (cons "script" args) CONFIGURATION)))
