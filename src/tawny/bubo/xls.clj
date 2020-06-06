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
   (seq (map vals
        (->> (load-workbook filename)
        (select-sheet sheet)
        (select-columns{column column})))))
  )


;; (defn xls-apply [f filename
;;                  & {:keys [from to sheet orientation header]
;;                     :or {sheet "Sheet1"
;;                          orientation :horizontal
;;                          header false}}]
;;   (doall
;;    (map
;;     #(apply f %)
;;     (read-workbook filename sheet))))

;; (defn xls-apply-v [f filename
;;                  & {:keys [sheet column orientation header]
;;                     :or {sheet "Sheet1"
;;                          orientation :vertical
;;                          header true}}]
;;   (doall
;;    (map
;;     #(apply f %)
;;     (read-workbookv filename sheet column))))

;; To apply the vertical reading of the sheet
;; (defn xls-apply [f sheet column]
;;   (doall
;;    (map
;;     #(apply f %)
;;     (column-info sheet column column))))

;;-------------
(defn- xls-apply-h [f filename
                    & {:keys [from to sheet header]
                       :or {sheet "Sheet1"
                            header false}}]
  (doall
   (map
    #(apply f %)
    (read-workbook filename sheet))))

(defn- xls-apply-v [f filename
                    & {:keys [sheet column header]
                       :or {sheet "Sheet1"
                            header true}}]
  (doall
   (map
    #(apply f %)
    (list (read-workbookv filename sheet column))))
)

(defn xls-apply [f filename
                 & {:keys [sheet column orientation header]
                    :or {sheet "Sheet1"
                         orientation :vertical
                         header true}}]

  (case orientation
    :horizontal (xls-apply-h f filename :sheet sheet :header
    header)

    :vertical (xls-apply-v f filename :sheet sheet
                        :column column)))
;;--------------
