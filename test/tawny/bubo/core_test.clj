(ns tawny.bubo.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh]
            [clojure.string :as s]
            [tawny.bubo.core :refer :all]))

(println "Loading core_test")

(when-not
    (.exists (io/file "sandbox"))
  (.mkdir (io/file "sandbox")))

(defonce
  ^{:doc "If non-nil do not clear sandbox files."}
  preserve-files nil)

;; Forms for local eval
;; (def preserve-files nil)
;; (def preserve-files true)

(defn- sandbox-command [command]
  (sh/sh
   "../bin/bubo"
   (str "../dev-resources/" command)
   :dir "sandbox"))

(defn- sandbox-command-no-error
  "Run commands from dev-resources in sandbox. "
  [command]
  (let [res (sandbox-command command)]
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
           (sandbox-command-no-error command)
           (sh/sh "diff"
                  (str "dev-resources/" output-file)
                  (str "sandbox/" output-file)))]
     (alter-var-root #'last-diff (constantly res))
     (when-not preserve-files
       (doall
        (map io/delete-file (drop 1 (file-seq (io/file "sandbox"))))))
     res)))

(defn out=
  "Return true if command produces a file identical to output-file."
  ([command]
   (out= (str command ".clj") (str command ".omn")))
  ([command output-file]
   (let [diff (diff-out command output-file)]
     (and (= 0 (:exit diff))
          (= "" (:out diff))
          (= "" (:err diff))))))

(defn fetch-error
  ([command]
   (let [retn
         (sandbox-command command)]
     (is (not= 0 (:exit retn)))
     (:err retn))))


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

(deftest empty-test
  (is (sandbox-command "empty.clj")))

(deftest error-test
  (let [errors (fetch-error "error.clj")]
    (is (s/includes? errors "Unable to resolve symbol: this-breaks"))
    (is (s/includes? errors "./dev-resources/error.clj:4:1"))))

(deftest clojure-core-empty-test
  (let [errors (fetch-error "clojure-core-empty.clj")]
    (is (s/includes? errors "Unable to resolve symbol: println"))
    (is (s/includes? errors "./dev-resources/clojure-core-empty.clj:3:1"))))

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
