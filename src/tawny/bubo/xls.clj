(ns tawny.bubo.xls
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   :dependencies [[dk.ative/docjure "1.10.0"]]))
(use 'dk.ative.docjure.spreadsheet)

(defn- read-workbook [filename sheet]
  (->> (load-workbook filename)
       (select-sheet sheet)
       row-seq
       (map cell-seq)
       (map #(map read-cell %))))

                                        ;(def workbook (load-workbook "xls-test.xlsx"))
                                        ;(def sheet (select-sheet "Sheet1" workbook))

(defn xls-apply [f filename sheet]
  (doall
   (map
    #(apply f %)
    (read-workbook filename sheet))))
