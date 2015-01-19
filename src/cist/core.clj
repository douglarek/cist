(ns cist.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :refer [file]]
            [clojure.pprint :as pp]
            [clojure.string :refer [join]])
  (:require [tentacles.gists :as gists])
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
       (join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(def auth
  (let [f (file (System/getProperty "user.home") ".cist")]
    (if (and (.exists f) (.canRead f)) (slurp (.getPath f))
        nil
      )
    )
  )

(defn ls-gists [auth]
  (let [gists (gists/gists {:oauth_token auth})]
    (doseq [g gists]
      (let [filesname (doall (map #(str (:filename %)) (vals (:files g))))]
        (println (format "%-50s%s" (:html_url g) (join ", " filesname))))
      )
    ))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (seq errors) (exit 1 (error-msg errors))
      (or (:help options) (every? empty? [arguments options])) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    (cond
      (seq arguments) (println arguments)
      (:list options) (ls-gists auth)
      )))
