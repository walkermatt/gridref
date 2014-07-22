(ns gridref.cli
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [clojure.math.numeric-tower :as math]
            [clojure.java.io :as io]
            [gridref.core :refer [parse-gridref parse-coord gridref2coord coord2gridref]]
            [gridref.util :refer [parse-figures]]))

(defn dispatch-cli
  [options args]
  (let [arg (if (nil? args) "" (first args))]
    (let [arg (if (= arg "-") (read-line) arg)]
      (if-let [gridref (parse-gridref arg)]
        (gridref2coord gridref)
        (if-let [coord (parse-coord arg)]
          (coord2gridref coord (:figures options)))))))

(def cli-options
  [["-f" "--figures <n>" "Number of figures to include in grid reference, an even number from 0 to 10"
    :default 10
    :parse-fn parse-figures
    :validate [#(and (>= % 0) (<= % 10) (= (mod % 2) 0))]]
   ["-h" "--help"]])

(defn usage-msg
  [options-summary]
  (format (slurp (io/resource "cli-usage")) options-summary))

(defn error-msg
  [e]
  (str "The following errors where found:" \newline (string/join \newline e)))

(defn process-cli
  "Process cli args returning a map of {:status <code> :output <string>}"
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) {:status 0 :output (usage-msg summary)}
      (not= (count arguments) 1) {:status 1 :output (usage-msg summary)}
      errors {:status 1 :output (error-msg errors)}
      :else (or (if-let [result (dispatch-cli options arguments)]
                  {:status 0 :output result})
                {:status 1 :output (usage-msg summary)}))))

(defn -main
  "Main entry point of cli app, handles the dirty work of printing output"
  [& args]
  (let [{:keys [status output]} (process-cli args)]
    (if (not= status 0)
      (do
        (binding [*out* *err*]
          (println output))
        (System/exit status))
      (println output))))

