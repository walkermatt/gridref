(ns gridref.core-test
  (:require [clojure.test :refer :all]
            [gridref.core :refer :all]))

(deftest test-to-int
  (testing "0 to int"
    (is (= (to-int "0") 0)))
  (testing "1 to int"
    (is (= (to-int "1") 1)))
  (testing "10 to int"
    (is (= (to-int "10") 10)))
  (testing "100 to int"
    (is (= (to-int "100") 100)))
  (testing "-1 to int"
    (is (= (to-int "-1") -1))))

(deftest test-char2n
  (testing "A = 0"
    (is (= (char2n \A) 0)))
  (testing "B = 1"
    (is (= (char2n \B) 1)))
  (testing "H = 7"
    (is (= (char2n \H) 7)))
  (testing "I = 7 (I is skipped)"
    (is (= (char2n \I) 7)))
  (testing "J = 8 (I is skipped so chars after I are one lower than expected"
    (is (= (char2n \J) 8))))

(deftest test-char2offset
  (testing "A = [0 0] (first col, first row)"
    (is (= (char2offset \A) [0.0 0.0])))
  (testing "B = [1 0]"
    (is (= (char2offset \B) [1.0 0.0])))
  (testing "C = [2 0]"
    (is (= (char2offset \C) [2.0 0.0])))
  (testing "D = [3 0]"
    (is (= (char2offset \D) [3.0 0.0])))
  (testing "E = [4 0] (last col, first row)"
    (is (= (char2offset \E) [4.0 0.0])))
  (testing "F = [0 1] (First col, second row)"
    (is (= (char2offset \F) [0.0 1.0])))
  (testing "Z = [4 4] (Last col, last row)"
    (is (= (char2offset \Z) [4.0 4.0]))))
(test-char2offset)

(def major-origin [-1000000.0 2000000.0])
(def major-cell-width 500000)

(deftest test-char2coord
  (testing "A = major-origin (top right)"
    (is (= (char2coord \A major-origin major-cell-width) major-origin)))
  (testing "B = [-500000.0 2000000.0] (top row, second cell)"
    (is (= (char2coord \B major-origin major-cell-width) [-500000.0 2000000.0])))
  (testing "E = [1000000.0 2000000.0] (top row, 5th and last cell)"
    (is (= (char2coord \E major-origin major-cell-width) [1000000.0 2000000.0])))
  (testing "F = [-1000000.0 1500000.0] (2nd row, first cell, the coords wrap around)"
    (is (= (char2coord \F major-origin major-cell-width) [-1000000.0 1500000.0])))
  (testing "K = [1000000.0 1500000.0] (2nd row, last cell)"
    (is (= (char2coord \K major-origin major-cell-width) [1000000.0 1500000.0])))
  (testing "Z = [1000000.0 0.0] (5th and last row, 5th and last col)"
    (is (= (char2coord \Z major-origin major-cell-width) [1000000.0 0.0]))))

(deftest test-alpha2coord
  (testing "AA (bottom right, coord of first row and column in major and minor)"
    (is (= (alpha2coord "AA") [-1000000.0 1900000.0])))
  (testing "ST"
    (is (= (alpha2coord "ST") [300000.0 100000.0])))
  (testing "SV"
    (is (= (alpha2coord "SV") [0.0 0.0])))
  (testing "HP"
    (is (= (alpha2coord "HP") [400000.0 1200000.0])))
  (testing "NZ"
    (is (= (alpha2coord "NZ") [400000.0 500000.0])))
  (testing "TG"
    (is (= (alpha2coord "TR") [600000.0 100000.0])))
  (testing "Lowercase letters"
    (is (= (alpha2coord "aa") [-1000000.0 1900000.0]))))
; (test-alpha2coord)

(deftest test-padn
  (testing "Empty string"
    (is (== (padn "" 5) 0)))
  (testing "Single digit"
    (is (== (padn "1" 5) 10000)))
  (testing "Two digits"
    (is (== (padn "12" 5) 12000)))
  (testing "Three digits"
    (is (== (padn "123" 5) 12300)))
  (testing "Four digits"
    (is (== (padn "1234" 5) 12340)))
  (testing "Five digits"
    (is (== (padn "12345" 5) 12345)))
  (testing "Six digits"
    (is (== (padn "123456" 5) 12345))))
; (test-padn)

(deftest test-grid2coord
  (testing "Ben Nevis"
    (is (= (grid2coord "NN1665071250") [216650.0 771250.0])))
  (testing "OS Southampton"
    (is (= (grid2coord "SU387148") [438700.0 114800.0])))
  (testing "Tower of London"
    (is (= (grid2coord "TQ336805") [533600.0 180500.0])))
  (testing "SU"
    (is (= (grid2coord "SU") [400000.0 100000.0])))
  (testing "Glasgow"
    (is (= (grid2coord "NS5899860113") [258998.0 660113.0])))
  (testing "Saxa Vord (Northern Shetland)"
    (is (= (grid2coord "HP6322316714") [463223.0 1216714.0])))
  (testing "St Marys Airport (Scilly Isles)"
    (is (= (grid2coord "SV9178010372") [91780.0 10372.0])))
  (testing "Close to the origin of British National Grid"
    (is (= (grid2coord "SV0239114892") [2391.0 14892.0]))))
; (test-grid2coord)
