(ns tawny.bubo.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [tawny.bubo.core :refer :all]))

(when-not
    (.exists (io/file "sandbox"))
  (.mkdir (io/file "sandbox")))

(defonce
  ^{:doc "If non-nil do not clear sandbox files."}
  preserve-files nil)

;; Forms for local eval
;; (def preserve-files nil)
;; (def preserve-files true)

(defn- sandbox-command
  "Run commands from dev-resources in sandbox. "
  [command]
  (let [res
        (sh/sh (str "../dev-resources/" command)
               :dir "sandbox")]
    (when-not (= 0 (:exit res))
      (throw (Exception.
              (str "Command: " command " produced error "
                   (:err res)))))
    res))

(defn- sandbox-file
  "Move file into sandbox."
  [file]
  (io/copy
   (io/file (str "./dev-resources/" file))
   (io/file (str "./sandbox/" file))))

(defmacro with-files [files & body]
  `(let [files# ~files]
     (doall
      (map sandbox-file
           (if (seq? files#)
             files#
             [files#])))
     ~@body))

(def ^:private last-diff nil)

(defn diff-out
  ([command]
   (diff-out (str command ".clj") (str command ".omn")))
  ([command output-file]
   (let [res
         (do
           (sandbox-command command)
           (sh/sh "diff"
                  (str "dev-resources/" output-file)
                  (str "sandbox/" output-file)))]
     (alter-var-root #'last-diff (constantly res))
     (when-not preserve-files
       (doall
        (map io/delete-file (drop 1 (file-seq (io/file "sandbox"))))))
     res)))

(defn out=
  ([command]
   (out= (str command ".clj") (str command ".omn")))
  ([command output-file]
   (let [diff (diff-out command output-file)]
     (and (= 0 (:exit diff))
          (= "" (:out diff))
          (= "" (:err diff))))))

(defn expand-out= [msg form]
  `(let [result#
         ~(if (= 3 (count form))
            `(out= ~(second form)
                   ~(nth form 2))
            `(out= ~(second form)))]
     (if result#
       (do-report {:type :pass, :message ~msg
                   :expected '~form, :actual true})
       (do-report {:type :fail, :message ~msg
                   :expected '~form,
                   :actual last-diff}))
     result#))

(defmethod clojure.test/assert-expr 'out= [msg form]
  (expand-out= msg form))

(deftest hello
  (is
   (out= "hello")))

(deftest ontology-and-class
  (is (out= "ontology-and-class")))

(deftest entity-pattern
  (is (out= "entity-pattern")))

(deftest apply-string
  (is (out= "apply-string")))

(deftest apply-csv
  (with-files "csv-to-apply.csv"
    (is (out= "apply-csv"))))
