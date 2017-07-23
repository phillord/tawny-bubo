#!/usr/bin/env bubo

(defontology o
  :iri "http://example.com/o")

(defpattern simple-pattern [a]
  (owl-class a))

(string-apply
 simple-pattern
 "a" "b" "c" "d")

(save :omn)
