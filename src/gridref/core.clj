(ns gridref.core
  (:require [clojure.string :as string]
            [clojure.math.numeric-tower :as math]
            [clojure.tools.trace :as trace])
  (:gen-class))

; Global vars
(def major-origin [-1000000.0 2000000.0])
(def major-cell-width 500000.0)
(def minor-cell-width 100000.0)

(defn to-int [s]
  (Integer/parseInt s))

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
    [(+ (nth origin 0) e) (- (nth origin 1) n)]))

(defn alpha2coord
  "Get the easting & northing coordinate pair associated with a pair of britsh
  national grid square characters."
  [r]
  (let [[major minor] r
        coord (offset2topright (char2offset minor) (offset2topright (char2offset major) major-origin major-cell-width) minor-cell-width)]
    (assoc coord 1 (- (get coord 1) minor-cell-width))))

(defn padn
  "Pad the given number so it is length long. Accepts a string or number,
  expects whole integers"
  [n length]
  (to-int (apply str (take 5 (concat (str n) (repeat 5 "0"))))))

(defn grid2coord
  "Convert a british national grid reference to an easting & northing
  coordinate pair as a vector: [easting northing]"
  [grid]
  (let [n (math/round (/ (- (count grid) 2.0) 2))]
    (let [re (re-pattern (str "([A-Z]{2})"
                              (if (> n 0)
                                (str "(\\d{" n "})(\\d{" n "})"))))]
      ; Split the gridref into it's parts, head is the grid letters,
      ; the tail is the numeric part which are set to 0 0 if not present
      (let [parts (nthnext (re-matches re grid) 1)
            alpha (first parts)
            numeric (if (== (count parts) 3) (drop 1 parts) ["0" "0"])]
        (into [] (map + (alpha2coord alpha) (map #(padn % 5) numeric)))))))

(defn -main
  "Passed an OS grid reference as an argument will return the eastings and northings."
  [& args]
  (if args
    (println (grid2coord (first args)))
    (println "Usage: gridref GRIDREF")))

;   col0   col1   col2   col3   col4
;   0      1      2      3      4      -   row0
;   5      6      7      8      9      -   row1
;   10     11     12     13     14     -   row2

