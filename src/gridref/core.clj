(ns gridref.core
  (:require [clojure.string :as string]
            [clojure.math.numeric-tower :as math]
            [clojure.tools.trace :as trace])
  (:gen-class))

(defn to-int [s]
  (Integer/parseInt s))

(defn char2n [c]
  (let [a (int (first (string/upper-case c)))
        b (if (>= a (int \I)) (dec a) a)]
    (- b (int \A))))

(defn char2offset [c]
  (let [n (char2n c)
        col (math/floor (/ n 5.0))
        row (+ (- n (* (inc col) 5.0)) 5.0)]
    [row col]))

(defn char2coord [c origin cellwidth]
  (let [[e n] (map #(* % cellwidth) (char2offset c))]
    [(+ (nth origin 0) e) (- (nth origin 1) n)]))

(defn alpha2coord [r]
  (let [[major minor] r
        coord (char2coord minor (char2coord major [-1000000.0 2000000.0] 500000) 100000)]
    (assoc coord 1 (- (get coord 1) 100000))))

(defn padn [n length]
  (to-int (apply str (take 5 (concat n (repeat 5 "0"))))))

(defn grid2coord
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

