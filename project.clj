(defproject cist "0.1.0"
  :description "A Clojure command-line wrapper with tentacles library to access GitHub Gist"
  :url "https://github.com/douglarek/cist"
  :license {:name "Copyright (c) 2015, Lingchao Xin"
            :url "https://raw.githubusercontent.com/douglarek/cist/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.7.0"] [tentacles "0.3.0"] [org.clojure/tools.cli "0.3.3"]]
  :main cist.core
  :omit-source true
  :aot [cist.core]
  )
