(defproject cist "1.0.1"
  :description "A Clojure command-line wrapper with tentacles library to access GitHub Gist"
  :url "https://github.com/douglarek/cist"
  :license {:name "Copyright (c) 2016, Lingchao Xin"
            :url "https://raw.githubusercontent.com/douglarek/cist/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"] [tentacles "0.5.1"] [org.clojure/tools.cli "0.3.5"] [clj-http "3.4.1"]]
  :main cist.core
  :omit-source true
  :aot [cist.core]
  )
