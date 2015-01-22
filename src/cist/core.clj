(ns cist.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :refer [file]]
            [clojure.pprint :as pp]
            [clojure.string :refer [join]])
  (:require [tentacles.gists :as gists])
  (:gen-class))

(def cli-options
  [
   ["-l" nil "List public gists, with `-A` list all ones" :id :list]
   ["-A" nil nil :id :all]
   ["-D" "--delete ID" "Detele an existing gist"]
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

(defn ls-gists [& {:keys [all-pages private?]}]
  (let [gists (gists/gists {:oauth_token auth :all-pages all-pages})]
    (doseq [g gists]
      (let [filesname (doall (map #(str (:filename %)) (vals (:files g))))]
        (when (not= private? (:public g))
          (println (format "%-50s%s" (:html_url g) (join ", " filesname))))
      )
      )))

(defn delete-gist [id]
  (let [result (gists/delete-gist id {:oauth-token auth})]
    (if result
      (println (format "Gist <%s> has been deleted successfully." id))
      (println (format "Failed to delete Gist <%s>" id))
      )
    )
  )

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (seq errors) (exit 1 (error-msg errors))
      (or (:help options) (every? empty? [arguments options])) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    (cond
      (seq arguments) (println arguments)
      (and (:list options) (:all options)) (ls-gists :all-pages true)
      (:list options) (ls-gists :all-pages true :private? true)
      (:delete options) (delete-gist (:delete options))
      )))
