#!/usr/bin/env bubo

(cc/use 'tawny.bubo.xls)


(defontology o
  :iri "http://example.com/o")

(defpattern xls-pattern
  [a b c & {:keys [sheet orientation from to column]
            :or {sheet "Sheet1"
                 orientation horizontal
                 from "A1"
                 to "C3"
                 column A}}]

 ;; This is to convert the row data into pattern as:
 ;; (owl-class a :super (some b c))
 (entity
   [(object-property b)
    (owl-class c)]
   (owl-class a :super (some b c)))

  ;; This is to read one column and convert it into teir
  (when column
    :orientation :vertical
    (doall
     (let [column-data
           (tawny.bubo.xls/column-info sheet column column)]
       (pattern/tier (first column-data) (rest column-data))))))

;; (doall
;;  (map (intern-owl-string % (owl-class %) args)))

;; (cc/let [first-class (owl-class (cc/first args))
;;       rst-class (owl-class (cc/rest args))]
;;   (tawny.pattern/tier first-class [rest-class]
;;     :functional false
;;     :cover false)))

;; (tawny.bubo.xls/column-classes
;;  ;(column-inf "Sheet3" :B)
;;  "xls-test.xlsx" "Sheet1" :A)


;; Apply the pattern horizontally
(xls-apply
 (xls-pattern :sheet "Sheet2" :orientaion :horizontal)
 "xls-test.xlsx"
 )

;; Apply the pattern vertically on the column B in Sheet3
(xls-apply
 (xls-pattern :sheet "Sheet3" :column :B)
 "xls-test.xlsx"
 "Sheet3")



(defoproperty inGenre)

(defclass Artist)
(defclass Genre)

(defpattern artist [artist genre website]
  (entity
   (owl-class genre :super Genre)
   (owl-class artist
              :super Artist (some inGenre genre)
              :annotation (see-also website))))

(xls-apply
 artist-pattern
 "xls-test.xlsx"
 :sheet "Sheet2" :orientation :horiztonal
 :from "A1" :to "C3" :header true)


(save :omn)

;; This is an example of the Excel data in Sheet3:
;;
;; Artists/Groups     Genre      Website
;; 1 0 AM             Pop        www.10am.bandcamp.com/
;; Alice Ella         Pop        www.aliceella.co.uk
;; Alina Bzhezhinska  Jazz       www.alina-harpist.com
;; Alison Beattie     Classical  www.alisonbeattiemusic.com
;; Alison Rayner      Jazz       www.alisonrayner.com
