(ns tawny.bubo.xls
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [tawny.owl :as o]
   [tawny.pattern :as pattern]))
(use 'dk.ative.docjure.spreadsheet)

(defn- read-workbook [filename sheet]
  "Read the Excel workbook giving the file name and the sheet of interest.
   FILENAME: the excel file name.
   SHEET: the sheet name to read."
  (->> (load-workbook filename)
       (select-sheet sheet)
       row-seq
       (remove nil?)
       (map cell-seq)
       (map #(map read-cell %))))

;; This is the workbook variaable
(def workbook (load-workbook "xls-test.xlsx"))
;; This is the sheet variable
(def sheet (select-sheet "Sheet1" workbook))

(defn column-info [sheet & {:keys [column]}]
  "Extract the column info into a sorted set
  SHEET: the sheet to be extracted.
  COLUMN: the column of interest."
  (map vals (select-columns {column column} sheet))
)

(defn column-classes [filename sheet column]
  "Convert the giving column data into a tier
  FILENAME: the excel file name.
  SHEET: the sheet to be extracted.
  COLUMN: the column to be converted to owl classes."
  ;(read-workbook filename sheet)
  (doall
   ;(read-workbook filename sheet)
    (let [x (column-info sheet column column)]
     (pattern/tier (first x) (rest x)))
     )
)

(defn xls-apply [f filename sheet]
  (doall
   (map
    #(apply f %)
    (read-workbook filename sheet))))

;; To apply the vertical reading of the sheet
;; (defn xls-apply [f sheet column]
;;   (doall
;;    (map
;;     #(apply f %)
;;     (column-info sheet column column))))
