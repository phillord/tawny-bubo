(ns tawny.bubo.core
  (:require [tawny.owl :as o]))

(def ^:dynamic *script-file* nil)

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
   (o/save-ontology
    (str (stem *script-file*) "." (name format)))))

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
