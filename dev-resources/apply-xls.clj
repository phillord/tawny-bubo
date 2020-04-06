#!/usr/bin/env bubo

(clojure.core/use 'tawny.bubo.xls)

(defontology o
  :iri "http://example.com/o")

(defpattern pattern [a r b]
  (entity
      [(object-property r)
       (owl-class b)]
    (owl-class a :super (some r b))))

(xls-apply
 pattern
 "xls-test.xlsx"
 "Sheet1")

(save :omn)
