(ns gridref.util
  (:require [clojure.math.numeric-tower :as math]))

(defn to-int [s]
  (Integer/parseInt s))

(defn to-float [s]
  (Float/parseFloat s))

(defn pad-head
  "Pad the given number with leading zeros so it is 5 digits long. Accepts a
  string or number, expects whole integers, returns a string"
  [n]
  (format (str "%05d") n))

(defn nearest-even
  "Return the nearest even number to n, rounds down"
  [n]
  (int (* (math/floor (/ n 2)) 2)))

(defn between
  [lower upper n]
  "If n is less than lower return lower, if n is greater than upper return
  upper, otherwise return n"
  (min upper (max lower n)))

(defn parse-figures
  "Parses a string representing the number of figures in a grid reference,
  defaults to 10 if the number can't be parsed"
  [figures]
  (try (int (let [n (Float/parseFloat figures)]
              (nearest-even (between 0 10 n))))
       (catch Exception e 10)))
