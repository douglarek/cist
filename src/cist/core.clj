(ns cist.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string])
  (:gen-class))

(def cli-options
  [
   ["-l" nil "List public gists"
    :id :list]
   [nil "--login" "Authenticate gist on this computer"]
   ["-h" "--help" "Show this message and exit"]
   ])

(defn usage [options-summary]
  (->> ["A Clojure command-line wrapper with tentacles library to access GitHub Gist."
        ""
        "Usage: cist [OPTIONS] [FILES]..."
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (seq errors) (exit 1 (error-msg errors))
      (or (:help options) (every? empty? [arguments options])) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    (cond
      (seq arguments) (println arguments)
      (:list options) (println "List public gists ...")
      )))
