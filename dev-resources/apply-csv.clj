#!/usr/bin/env bubo

(clojure.core/use 'tawny.bubo.csv)

(defontology o
  :iri "http://example.com/o"
  :noname true)

(defpattern pattern [a r b]
  (entity
      [(object-property r)
       (owl-class b)]
    (owl-class a :super (some r b))))

(clojure.core/defn pattern [a r b]
  (clojure.core/let
      [r (object-property r)
       b (owl-class b)]
    (owl-class a :super (some r b))))

(csv-apply
 pattern
 "csv-to-apply.csv")

(save :omn)
