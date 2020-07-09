#!/usr/bin/env bubo

(cc/use 'tawny.bubo.xls)

;; Define an ontology to save all entities in it.
(defontology o
  :iri "http://example.com/o"
  :noname true)

;; (defpattern xls-pattern
;;   [a b c & {:keys [sheet orientation from to column]
;;             :or {sheet "Sheet1"
;;                  orientation horizontal
;;                  from "A1"
;;                  to "C3"
;;                  column A}}]

;;  ;; This is to convert the row data into pattern as:
;;  ;; (owl-class a :super (some b c))
;;  (entity
;;    [(object-property b)
;;     (owl-class c)]
;;    (owl-class a :super (some b c)))

;;   ;; This is to read one column and convert it into teir
;;   (when column
;;     :orientation :vertical
;;     (doall
;;      (let [column-data
;;            (tawny.bubo.xls/column-info sheet column column)]
;;        (pattern/tier (first column-data) (rest column-data))))))

;; (doall
;;  (map (intern-owl-string % (owl-class %) args)))

(defoproperty inGenre)

(defclass Artist)
(defclass Genre)

(defpattern artist-pattern [artist genre website]
  (entity
   [(owl-class genre :super Genre)
    (owl-class artist
               :super Artist (some inGenre genre)
               :annotation (see-also website))]))

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
  (tawny.pattern/tier (cc/first column-values)(cc/rest
  column-values)))

 ;; (cc/defn dump [& args]
 ;;   (cc/println args))

;; apply header-individuals pattern where the header values are
;; classes and the rest are inviduals
(xls-apply
 header-indviduals
 "xls-test.xlsx"
 :sheet "Sheet1" :orientation :horizontal :header true)

(xls-apply
 header-indviduals
 "xls-test.xlsx"
 :sheet "Sheet2" :orientation :horizontal :header true)

;; apply the column-pattern
(xls-apply
 column-pattern
 "xls-test.xlsx"
 :sheet "Sheet3" :column :B :orientation :vertical :header true)

;; (xls-apply
;;  body-indviduals
;;  "xls-test.xlsx"
;;  :sheet "Sheet1" :orientation :horizontal :header header-classes)

;; (xls-apply
;;  (cc/partial my-ontology column-pattern)
;;  "xls-test.xlsx" :sheet "Sheet 4")

(save :omn)

;; This is an example of the Excel data in Sheet3:
;;
;; Artists/Groups     Genre      Website
;; 1 0 AM             Pop        www.10am.bandcamp.com/
;; Alice Ella         Pop        www.aliceella.co.uk
;; Alina Bzhezhinska  Jazz       www.alina-harpist.com
;; Alison Beattie     Classical  www.alisonbeattiemusic.com
;; Alison Rayner      Jazz       www.alisonrayner.com
