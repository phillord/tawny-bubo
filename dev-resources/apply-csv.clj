#!/usr/bin/env bubo

(clojure.core/use 'tawny.bubo.csv)

(defontology o
  :iri "http://example.com/o")

(defpattern pattern [a r b]
  (entity
      [(object-property r)
       (owl-class b)]
    (owl-class a :super (some r b))))

(csv-apply
 pattern
 "csv-to-apply.csv")


(save :omn)
