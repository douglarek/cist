(ns cist.core
  (:require [clojure.java.io :refer [file]]
            [clojure.string :refer [join trim]]
            [clojure.tools.cli :refer [parse-opts]])
  (:require [tentacles.gists :as gists])
  (:use [tentacles.oauth :only [create-auth]])
  (:gen-class))

(def cli-options
  [["-l" nil "List public gists, with `-A` list all ones" :id :list]
   ["-A" nil nil :id :all]
   ["-d" "--description DESCRIPTION" "Adds a description to your gist"]
   ["-D" "--delete ID" "Detele an existing gist"]
   [nil "--login" "Authenticate gist on this computer"]
   ["-p" nil "Makes your gist private" :id :public]
   ["-h" "--help" "Show this message and exit"]])

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

(def cist-home (join "/" [(System/getProperty "user.home") ".cist"]))

(def read-token
  (let [f (file cist-home)]
    (if (and (.exists f) (.canRead f)) (trim (slurp (.getPath f)))
        nil)))

(defn ls-gists [& {:keys [all-pages private?]}]
  (let [gists (gists/gists {:oauth_token read-token :all-pages all-pages})]
    (doseq [g gists]
      (let [filesname (doall (map #(str (:filename %)) (vals (:files g))))]
        (when (not= private? (:public g))
          (println (format "%-50s%s" (:html_url g) (join ", " filesname))))))))

(defn delete-gist [id]
  (let [result (gists/delete-gist id {:oauth-token read-token})]
    (if (true? result)
      (println (format "Gist <%s> has been deleted successfully." id))
      (println "Failed"))))

(defn- files-contents [files]
  (let [fs (set files)]
    (map #(vector (.getName (file %)) (slurp (.getPath (file %)))) fs)))

(defn create-gist [files & {:keys [description public] :or {description "" public true}}]
  (let [result (gists/create-gist (files-contents files) {:oauth-token read-token :description description :public public})]
    (if (:id result)
      (println (:html_url result))
      (println result))))

(defn login []
  (let [f (fn [x] (print x) (flush) (read-line))]
    (let [user (f "GitHub username: ")
          pass (String/valueOf (.readPassword (System/console) "GitHub password: " nil))
          r (create-auth {:note "gist" :scopes ["gist"] :auth [user pass] :fingerprint (System/currentTimeMillis)})
          t (:token r)]
      (if (some? t)
        (spit cist-home t)
        (println (:message (:body r)))))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (seq errors) (exit 1 (error-msg errors))
      (or (:help options) (every? empty? [arguments options])) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    (cond
      (seq arguments) (create-gist arguments :public (not (:public options)) :description (:description options))
      (:list options) (ls-gists :all-pages true :private? (or (not (:all options)) nil))
      (:delete options) (delete-gist (:delete options))
      (:login options) (login))))
