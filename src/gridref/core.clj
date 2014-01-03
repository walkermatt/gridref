(ns gridref.core
  (:require [clojure.string :as string]
            [clojure.math.numeric-tower :as math]
            [clojure.tools.trace :as trace]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.java.io :as io])
  (:gen-class))

; Global vars
(def major-origin [-1000000.0 2000000.0])
(def major-cell-width 500000.0)
(def minor-cell-width 100000.0)

;; Utility

(defn to-int [s]
  (Integer/parseInt s))

(defn pad-head
  "Pad the given number with leading zeros so it is 5 digits long. Accepts a
  string or number, expects whole integers, returns a string"
  [n]
  (format (str "%05d") n))

(defn pad-tail
  "Pad the given number with digits so it is 5 digits long. Accepts number,
  expects a whole int, returns an int"
  [n]
  (to-int (apply str (take 5 (concat (str n) (repeat 5 "0"))))))

;; Grid reference to coord

(defn char2cell
  "Returns numeric position of a given character relative to A. The character I
  is skipped as it's not a valid grid reference character. A = 0, B = 1 etc."
  [c]
  (let [n (int (first (string/upper-case c)))]
    (- (if (>= n (int \I)) (dec n) n) (int \A))))

(defn cell2offset
  "Returns the row and column that a given numbered cell falls at in a five
  by five grid."
  [n]
  (let [col (math/floor (/ n 5.0))
        row (+ (- n (* (inc col) 5.0)) 5.0)]
    [row col]))

(defn char2offset
  "Get the offset in a five by five grid of a given character, where each grid
  cell is assigned a character A -> Z left to right, top to bottom skipping I"
  [c]
  (cell2offset (char2cell c)))

; TODO should this be offset2coord and calculate the bottom left?
(defn offset2topright
  "Get the easting & northing coordinate pair associated with a given offset
  in a five by five grid relative to the specified origin and cellwidth (both in
  meters)."
  [offset origin cellwidth]
  (let [[e n] (map #(* % cellwidth) offset)]
    [(+ (first origin) e) (- (second origin) n)]))

(defn alpha2coord
  "Get the easting & northing coordinate pair associated with a pair of britsh
  national grid square characters."
  [r]
  (let [[major minor] r
        coord (offset2topright (char2offset minor) (offset2topright (char2offset major) major-origin major-cell-width) minor-cell-width)]
    (assoc coord 1 (- (get coord 1) minor-cell-width))))

(defn gridref2coord
  "Convert a british national grid reference to an easting & northing
  coordinate pair as a vector: [easting northing]"
  [grid]
  (let [grid (string/replace grid " " "")
        n (quot (- (count grid) 2) 2)]
    (let [re (re-pattern (str "([a-zA-Z]{2})"
                              (if (> n 0)
                                (str "(\\d{" n "})(\\d{" n "})"))))]
      ; Split the gridref into it's parts, head is the grid letters,
      ; the tail is the numeric part which are set to 0 0 if not present
      (if-let [parts (nthnext (re-matches re grid) 1)]
        (let [alpha (first parts)
              numeric (if (== (count parts) 3) (drop 1 parts) ["0" "0"])]
          (into [] (map + (alpha2coord alpha) (map pad-tail numeric))))))))

;; Coordinate to grid reference

(defn cell2char
  "Get the character associated with the numeric position relative to A."
  [n]
  (let [n (+ (int \A) n)]
    (char (if (>= n (int \I)) (inc n) n))))

(defn coord2offset
  "Get the offset in a five by five grid relative to the specified origin and
  cellwidth (both in meters) for the given easting & northing coordinate pair.
  Called once to get the first grid letter and again to get the second."
  [coord origin cellwidth]
  (let [[e w] (map #(- % (mod % cellwidth)) coord)]
    [(math/floor (/ (- e (first origin)) cellwidth))
     (math/floor (/ (- (second origin) w minor-cell-width) cellwidth))]))

(defn offset2cell
  "Get the number of the cell in a five by five grid counting from left to
  right, top to bottom"
  [o]
  (+ (first o) (* (second o) 5)))

(defn coord2alpha
  "Get the first two grid reference letters for a given easting & northing
  coordinate pair."
  [coord]
  (let [major (cell2char (offset2cell (coord2offset coord major-origin major-cell-width)))
        minor-origin (offset2topright (char2offset major) major-origin major-cell-width)
        minor (cell2char (offset2cell (coord2offset coord minor-origin minor-cell-width)))]
    (str major minor)))

(defn coord2digits
  "Get the trailing digits for an alpha numberic grid reference from a coordinate pair"
  [coord figures]
  (let [n (/ figures 2)]
    (apply str (map #(apply str (take n (pad-head (int (mod % minor-cell-width))))) coord))))

(defn coord2gridref
  "Get a five figure grid reference for a given coordinate."
  [coord figures]
    (str (coord2alpha coord) (coord2digits coord figures)))

;; CLI

(def gridref-re #"(^[a-zA-Z]{2}(?: ?\d+ ?\d+)?)")
(defn parse-gridref
  "Return a valid grid reference or nil"
  [gridref]
  (if-let [match (re-find gridref-re gridref)]
    (second match)))

(def coord-re #"^\[?(\d+)(?:\.\d+)?[ ,]+(\d+)(?:\.\d+)?\]?")
(defn parse-coord
  "Parse a string representing a coordinate pair and return a valid coord
  vector [easting northing] or nil"
  [coord]
  (if-let [match (re-find coord-re coord)]
    (map to-int (drop 1 match))))

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
     :parse-fn #(to-int %)
     :validate [#(and (>= % 0) (<= % 10) (= (mod % 2) 0))]]
    ["-h" "--help"]])

(defn usage-msg
 [options-summary]
 (format (slurp (io/resource "cli-usage")) options-summary))

(defn error-msg
  [e]
  (str "The following errors where found:" \newline (string/join \newline e)))

(defn process-cli
  [args]
    (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
      (cond
        (:help options) (usage-msg summary)
        (not= (count arguments) 1) (usage-msg summary)
        errors (error-msg errors)
        :else (or (dispatch-cli options arguments) (usage-msg summary)))))

(defn -main
  "Passed an OS grid reference as an argument will return the eastings and northings."
  [& args]
  (println (process-cli args)))

;   col0   col1   col2   col3   col4
;   0      1      2      3      4      -   row0
;   5      6      7      8      9      -   row1
;   10     11     12     13     14     -   row2
