(ns tawny.bubo.core
  (:require [tawny.owl :as o]))

(def ^:dynamic *script-file* nil)
(def ^:dynamic *opt-to-std-out* nil)

(defmacro entity
  "Add type information to parameters for a pattern."
  {:style/indent 1}
  [bindings & body]
  `(let [
         ~@(interleave
            (map second bindings)
            bindings)]
     ~@body))

(defmacro defpattern [& body]
  `(defn ~@body))

(defn- stem [filename]
  (first
   (clojure.string/split
    (last
     (clojure.string/split filename #"[/]"))
    #"[.]")))

(defn save
  ([]
   (save :owl))
  ([format]
   (let [file
         (if *opt-to-std-out*
           (.toString (java.io.File/createTempFile "bubo" (str "." (name format))))
           (str (stem *file*) "." (name format)))]
     (o/save-ontology file format (if *opt-to-std-out* "" nil))
     (when *opt-to-std-out* (println (slurp file))))))

;; make defpattern add metadata?
(defn- arity
  [f]
  (let [invokes (filter #(= "invoke" (.getName %1)) (.getDeclaredMethods (class f)))]
  (apply max (map #(alength (.getParameterTypes %1)) invokes))))

(defn string-apply
  [f num-or-first & strings]
  (let [num
        (if (number? num-or-first)
          num-or-first
          (arity f))
        strings
        (if (number? num-or-first)
          strings
          (cons num-or-first strings))]
    (doall
     (map
      #(apply f %)
      (partition num strings)))))
