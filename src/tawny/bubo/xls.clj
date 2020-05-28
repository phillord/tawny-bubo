(ns tawny.bubo.xls
  (:use [dk.ative.docjure.spreadsheet])
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [tawny.owl :as o]
   [tawny.pattern :as pattern]))

(defn- read-workbook [filename sheet]
  "Read the Excel workbook horizontally giving the file name and the
  sheet of interest.
   FILENAME: the excel file name.
   SHEET: the sheet name to read."
  (->> (load-workbook filename)
       (select-sheet sheet)
       row-seq
       (remove nil?)
       (map cell-seq)
       (map #(map read-cell %))))

(defn- read-workbookv [filename sheet column]
  "Read the Excel workbook vertically giving the file name, sheet and
  the column of interest.
   FILENAME: the excel file name.
   SHEET: the sheet name to read.
   COLUMN: the column."
  (flatten
   (map vals
        (->> (load-workbook filename)
        (select-sheet sheet)
        (select-columns{column column})))))

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

(defn xls-apply [f filename
                 & {:keys [from to sheet orientation header]
                    :or {sheet "Sheet1"
                         orientation :horizontal
                         header false}}]
  (doall
   (map
    #(apply f %)
    (read-workbook filename sheet))))

(defn xls-apply-v [f filename
                 & {:keys [sheet column orientation header]
                    :or {sheet "Sheet1"
                         orientation :vertical
                         header true}}]
  (doall
   (map
    #(apply f %)
    (read-workbookv filename sheet column))))

;; To apply the vertical reading of the sheet
;; (defn xls-apply [f sheet column]
;;   (doall
;;    (map
;;     #(apply f %)
;;     (column-info sheet column column))))
