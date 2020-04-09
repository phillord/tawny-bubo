(ns tawny.bubo.cli
  (:require
   [clojure.main]
   [clojure.string]
   [tawny.bubo.core])
  (:gen-class))

(def cli-header "
(in-ns 'bubo)
(clojure.core/require '[clojure.core :as cc])
(cc/use 'tawny.bubo.core)
(cc/use 'tawny.bubo.util)
(cc/use 'tawny.owl)
(cc/use 'tawny.english)"  )

(defn- stem [filename]
  (first
   (clojure.string/split
    (last
     (clojure.string/split filename #"[/]"))
    #"[.]")))

(defn -main-old [& args]
  (let [file (first args)
        shebang-less-file
        (.getPath
         (java.io.File/createTempFile (stem file) ".clj"))
        eval-string
        (clojure.string/join "\n"
                             (cons
                              cli-header
                              (rest
                               (clojure.string/split-lines
                                (slurp file)))))]
    (spit shebang-less-file eval-string)
    (binding [tawny.bubo.core/*script-file* file]
      (load-file shebang-less-file))))

(defn blitz-clojure-core [ns]
  (doseq
      [[k v] (ns-map ns)
       :when (= (find-ns 'clojure.core) (:ns (meta v)))]
    (ns-unmap ns k)))

(defn -main [& args]
  (clojure.main/with-bindings
    (in-ns 'user)
    (blitz-clojure-core *ns*)
    (require '[clojure.core :as cc])
    (use 'tawny.bubo.core)
    (use 'tawny.bubo.util)
    (use 'tawny.owl)
    (use 'tawny.english)
    (clojure.main/load-script (first args))))
