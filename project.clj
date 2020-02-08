(defproject uk.org.russet/tawny-bubo "0.1.0-SNAPSHOT"
  :description "Automated Scripting Framework for Tawny-OWL"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [uk.org.russet/tawny-owl "2.0.0"]]

  :license {:name "LGPL"
            :url "http://www.gnu.org/licenses/lgpl-3.0.txt"
            :distribution :repo}

  :aliases
  ;; alias test to also do install; bubo works through the command line and finds
  ;; the rest of the library from maven, so we must install before we run tests.
  {"test" ["do" ["install"] "test"]})
