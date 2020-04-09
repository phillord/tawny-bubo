(ns tawny.bubo.util)


(defn blitz-clojure-core [ns]
  (doseq
      [[k v] (ns-map ns)
       :when (= (find-ns 'clojure.core) (:ns (meta v)))]
    (ns-unmap ns k)))

(defn prepare [ns]
  (clojure.core/use 'tawny.owl)
  (clojure.core/use 'tawny.english)
  (clojure.core/use 'tawny.bubo.core)
  (blitz-clojure-core ns)
  )
