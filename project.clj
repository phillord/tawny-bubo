(defproject uk.org.russet/tawny-bubo "0.3.1-SNAPSHOT"
  :description "Automated Scripting Framework for Tawny-OWL"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.csv "0.1.4"]
                 [uk.org.russet/tawny-owl "2.0.0"]
                 [org.slf4j/slf4j-nop "1.7.30"]
                 ]

  :license {:name "LGPL"
            :url "http://www.gnu.org/licenses/lgpl-3.0.txt"
            :distribution :repo}

  :plugins [[lein-file-replace "0.1.0"]]
  :release-tasks
  [["vcs" "assert-committed"]
   ["change" "version" "leiningen.release/bump-version" "release"]

   ["file-replace" "bin/bubo" "tawny-bubo \"" "\"]" "version"]

   ["vcs" "commit"]
   ["vcs" "tag"]
   ["deploy"]
   ["change" "version" "leiningen.release/bump-version"]
   ["vcs" "commit"]
   ["vcs" "push"]]

  :aliases
  ;; alias test to also do install; bubo works through the command line and finds
  ;; the rest of the library from maven, so we must install before we run tests.
  {"test" ["do" ["install"] "test"]})
