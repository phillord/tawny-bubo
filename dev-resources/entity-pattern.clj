#!/usr/bin/env bubo

(defontology o
  :iri "http://example.com/o")

(defpattern from-strings [a r c]
  (entity
      [(object-property r)
       (owl-class c)]
    (owl-class a :super (owl-some r c))))

(from-strings "a" "r" "c")
(from-strings "a1" "r1" "c1")
(from-strings "a2" "r2" "c2")
(from-strings "a3" "r3" "c3")
(from-strings "a4" "r4" "c4")

(save :omn)
