#!/usr/bin/env bubo

(cc/use 'tawny.bubo.xls)

;; Define an ontology to save all entities in it.
(defontology icbo
  :iri "http://example.com/icbo"
  :noname true)

(defpattern header-indviduals
  "To define the header of the table as classes and the rest as individuals.
  SHEET-VALUES the valuse extracted off the sheet in the excel file."
  [& sheet-values]
  (cc/let [header (cc/first sheet-values)
           individuals (cc/flatten (cc/rest sheet-values))]
    ;(cc/println "header: " header)
    ;(cc/println "individuals: " individuals)
    (cc/doall (cc/map owl-class header))
    ;;(cc/println individuals)
    (cc/doall (cc/map individual individuals)))
)

(defpattern column-pattern
  "This pattern takes a columnn data and creates a tier.
  COLUMN-VALUES: a sequence of the column data."
  [& column-values]

  (cc/println "See: " (cc/first column-values))
  (tawny.pattern/tier (cc/first column-values)(cc/rest column-values))
)

 ;; (cc/defn dump [& args]
 ;;   (cc/println args))

;; apply header-individuals pattern where the header values are
;; classes and the rest are inviduals
;; (xls-apply
;;  header-indviduals
;;  "xls-test.xlsx"
;;  :sheet "Sheet2" :orientation :horizontal :header true)

;; apply the column-pattern
;; (xls-apply
;;  column-pattern
;;  "xls-test.xlsx"
;;  :sheet "Sheet3" :column :B :orientation :vertical :header true)

;; ICBO sheet application:
(xls-apply
 header-indviduals
 "ICBO2017-Committee.xlsx"
 :sheet "ICBO_17_committee"
 :orientation :horizontal :header true)

;; (xls-apply
;;   column-pattern
;;   "ICBO2017-Committee.xlsx"
;;   :sheet "ICBO_17_committee"
;;   :column :B :orientation :vertical :header true)

;; (xls-apply
;;  column-pattern
;;  "ICBO2017-Committee.xlsx"
;;  :sheet "ICBO_17_committee"
;;  :column :C :orientation :vertical :header true)

;; (xls-apply
;;  column-pattern
;;  "ICBO2017-Committee.xlsx"
;;  :sheet "ICBO_17_committee"
;;  :column :D :orientation :vertical :header true)

;; Musical sheet application:
;; (xls-apply
;;  column-pattern
;;  "The F-List of UK Female Musicians & Writers1.xlsx"
;;  :sheet "UK ClassicalMediaPop Publishers"
;;  :column :A :orientation :vertical :header true)

;; (xls-apply
;;  column-pattern
;;  "The F-List of UK Female Musicians & Writers1.xlsx"
;;  :sheet "UK ClassicalMediaPop Publishers"
;;  :column :B :orientation :vertical :header true)

;; (xls-apply
;;  column-pattern
;;  "The F-List of UK Female Musicians & Writers1.xlsx"
;;  :sheet "UK ClassicalMediaPop Publishers"
;;  :column :C :orientation :vertical :header true)

;; and use two columns from another sheet:
;; (xls-apply
;;  column-pattern
;;  "The F-List of UK Female Musicians & Writers1.xlsx"
;;  :sheet "UK Self-Releasing Artists&Compo"
;;  :column :A :orientation :vertical :header true)

;; (xls-apply
;;  column-pattern
;;  "The F-List of UK Female Musicians & Writers1.xlsx"
;;  :sheet "UK Self-Releasing Artists&Compo"
;;  :column :B :orientation :vertical :header true)

;; Test sheet:
;; (xls-apply
;;  header-indviduals
;;  "xls-test.xlsx"
;;  :sheet "Sheet1" :orientation :horizontal :header true)

;; (xls-apply
;;  column-pattern
;;  "xls-test.xlsx"
;;  :sheet "Sheet3" :column :B :orientation :vertical :header true)

;; (xls-apply
;;  (cc/partial my-ontology column-pattern)
;;  "xls-test.xlsx" :sheet "Sheet 4")

(save :omn)
;;(save :owl)
