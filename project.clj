(defproject cist "0.1.0-SNAPSHOT"
  :description "A Clojure command-line wrapper with tentacles library to access GitHub Gist"
  :url "https://github.com/douglarek/cist"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"] [tentacles "0.3.0"] [org.clojure/tools.cli "0.3.1"]]
  :main cist.core
  :aot [cist.core]
)
