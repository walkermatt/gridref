(ns gridref.core
  (:require [clojure.string :as string]
            [clojure.math.numeric-tower :as math]
            [clojure.tools.trace :as trace]
            [gridref.util :as util])
  (:gen-class))

; Global vars
(def major-origin [-1000000.0 2000000.0])
(def major-cell-width 500000.0)
(def minor-cell-width 100000.0)

;; Utility

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
    (update-in coord [1] #(- % minor-cell-width))))

(defn bearing2digit
  [bearing]
  (get {"N" "5" "E" "5"} bearing "0"))

(defn tail2n
  [digit bearing]
  (util/to-float (apply str (take 5 (concat digit (bearing2digit bearing) "0000")))))

(defn partition-str
  [n coll]
  (map (partial apply str) (partition n coll)))

(defn tail2coord
  [digits bearing]
  (let [digits (concat (partition-str (quot (count digits) 2) digits) [nil nil])
        bearing (concat (reverse (partition-str 1 bearing)) [nil nil])]
    (take 2 (map tail2n digits bearing))))

(def gridref-re #"(^[A-Z]{2}(?: ?\d+ ?\d+)? ?(?:[NESW]{2})?)")
(defn parse-gridref
  "Return a valid grid reference or nil"
  [gridref]
  (let [gridref (string/upper-case (string/replace gridref " " ""))]
    (if-let [match (re-find gridref-re gridref)]
      (second match))))

(defn gridref2coord
  "Convert a british national grid reference to an easting & northing
  coordinate pair as a vector: [easting northing]"
  [grid]
  (if-let [grid (parse-gridref grid)]
    (let [parts (drop 1 (re-find #"([A-Z]{2})(\d+)?([NSEW]{2})?" grid))]
      (if-let [alpha (first parts)]
        (into [] (map + (alpha2coord alpha) (tail2coord (second parts) (last parts))))))))

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
  (let [[e n] (map #(- % (mod % cellwidth)) coord)]
    [(math/floor (/ (- e (first origin)) cellwidth))
     (math/floor (/ (- (second origin) n minor-cell-width) cellwidth))]))

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
    (apply str (map #(apply str (take n (util/pad-head (int (mod % minor-cell-width))))) coord))))

(defn coord2gridref
  "Get a five figure grid reference for a given coordinate."
  [coord figures]
  (str (coord2alpha coord) (coord2digits coord figures)))

;; CLI

(def coord-re #"^\[?(\d+)(?:\.\d+)?[ ,]+(\d+)(?:\.\d+)?\]?")
(defn parse-coord
  "Parse a string representing a coordinate pair and return a valid coord
  vector [easting northing] or nil"
  [coord]
  (if-let [match (re-find coord-re coord)]
    (vec (map util/to-float (drop 1 match)))))

;   col0   col1   col2   col3   col4
;   0      1      2      3      4      -   row0
;   5      6      7      8      9      -   row1
;   10     11     12     13     14     -   row2
