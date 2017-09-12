(ns tawny.bubo.csv
  (:require
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]))

(defn- read-csv [filename]
  (with-open [reader (io/reader filename)]
    (doall
     (csv/read-csv reader))))

(defn csv-apply [f filename]
  (doall
   (map
    #(apply f %)
    (read-csv filename))))
