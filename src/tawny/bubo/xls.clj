;; The contents of this file are subject to the LGPL License, Version 3.0.
;;
;; Copyright (C) 2020, Newcastle University
;;
;; This tool reads information from from an Excel file and turn them
;; into an ontology using pre-defined patterns.
;;
;; There are two ways to read the info in the Excel spreadsheet;
;; either horizontally row-by-row or vertically by reading the
;; specified column data.

(ns tawny.bubo.xls
  (:use [dk.ative.docjure.spreadsheet])
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [tawny.owl :as o]
   [tawny.pattern :as pattern]
   [tawny.read :as read]))

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

  (map read/stop-characters-transform ;to remove spaces
  (flatten
   ;(map read/stop-characters-transform
   (map vals
        (->> (load-workbook filename)
        (select-sheet sheet)
        (select-columns{column column})))))
 )

;;-------------
(defn- xls-apply-h
  "This applys the horizontal pattern function to an excel data.
  F is the name of the function to be applied.
  FILENAME the name the file to be parsed.

  The keywords arguments help to specify the information for the concise read.
  :from to specify the start cell of the range of intrest.
  :to is the end cell in the range of the intrest.
  :sheet the sheet that is to be read.
  :header to specify whether the first row of the range is a header of
  the data. The default is false."

  [f filename & {:keys [from to sheet header]
                 :or {sheet "Sheet1"
                      header false}}]
  (doall
   (map
    #(apply f %)
    (list (read-workbook filename sheet)))))

(defn- xls-apply-v
  "This applys the virtical pattern function to an excel data.
  F is the name of the function to be applied.
  FILENAME the name the file to be parsed.

  The keywords arguments help to specify the information for the concise read.
  :sheet the sheet that is to be read.
  :column this is the column of intrest.
  :header to specify whether the first row of the range is a header of
  the data. The default is true since this to make the colum data as a tier."

  [f filename & {:keys [sheet column header]
                 :or {sheet "Sheet1"
                      header true}}]
  (doall
   (map
    #(apply f %)
     ;; returns list of column data
    (list (read-workbookv filename sheet column))))
)

(defn xls-apply
  "This function wraps the previous two horizontal and vertical applying
  of the data.
  F is the name of the function to be applied.
  FILENAME the name the file to be parsed.

  The keywords arguments here to specify the orientation needed and
  other information for the concise read.
  :sheet the sheet that is to be read.
  :column this is the column of intrest.
  :orietation to specify the applying pattern horizontal or vertical.
  :header to specify whether the first row of the range is a header of
  the data. The default is true since this to make the colum data as a tier."

  [f filename & {:keys [sheet column orientation header]
                 :or {sheet "Sheet1"
                      orientation :vertical
                      header true}}]

  (case orientation
    :horizontal (xls-apply-h f filename :sheet sheet :header
    header)

    :vertical (xls-apply-v f filename :sheet sheet
                        :column column)))
;;--------------
