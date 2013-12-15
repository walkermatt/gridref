(ns gridref.core-test
  (:require [clojure.test :refer :all]
            [gridref.core :refer :all]))

;; Utility

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
;; (test-to-int)

(deftest test-pad-tail
  (testing "Empty string"
    (is (== (pad-tail "") 0)))
  (testing "Single digit"
    (is (== (pad-tail "1") 10000)))
  (testing "Two digits"
    (is (== (pad-tail "12") 12000)))
  (testing "Three digits"
    (is (== (pad-tail "123") 12300)))
  (testing "Four digits"
    (is (== (pad-tail "1234") 12340)))
  (testing "Five digits"
    (is (== (pad-tail "12345") 12345)))
  (testing "Six digits"
    (is (== (pad-tail "123456") 12345)))
  (testing "Pass a number"
    (is (== (pad-tail 123456) 12345))))
; (test-pad-tail)


(deftest test-char2cell
  (testing "A = 0"
    (is (= (char2cell \A) 0)))
  (testing "B = 1"
    (is (= (char2cell \B) 1)))
  (testing "H = 7"
    (is (= (char2cell \H) 7)))
  (testing "I = 7 (I is skipped)"
    (is (= (char2cell \I) 7)))
  (testing "J = 8 (I is skipped so chars after I are one lower than expected"
    (is (= (char2cell \J) 8))))
; (test-char2cell)

(deftest test-cell2offset
  (testing "0 = [0 0] (first col, first row)"
    (is (= (cell2offset 0) [0.0 0.0])))
  (testing "1 = [1 0]"
    (is (= (cell2offset 1) [1.0 0.0])))
  (testing "2 = [2 0]"
    (is (= (cell2offset 2) [2.0 0.0])))
  (testing "3 = [3 0]"
    (is (= (cell2offset 3) [3.0 0.0])))
  (testing "4 = [4 0] (last col, first row)"
    (is (= (cell2offset 4) [4.0 0.0])))
  (testing "5 = [0 1] (First col, second row)"
    (is (= (cell2offset 5) [0.0 1.0])))
  (testing "24 = [4 4] (Last col, last row)"
    (is (= (cell2offset 24) [4.0 4.0]))))
; (test-cell2offset)

(deftest test-char2offset
  (testing "A = major-origin (top right)"
    (is (= (char2offset \A) [0.0 0.0])))
  (testing "B = [1.0 0.0] (top row, second cell)"
    (is (= (char2offset \B) [1.0 0.0])))
  (testing "E = [4.0 0.0] (top row, 5th and last cell)"
    (is (= (char2offset \E) [4.0 0.0])))
  (testing "F = [0.0 1.0] (2nd row, first cell, the coords wrap around)"
    (is (= (char2offset \F) [0.0 1.0])))
  (testing "K = [4.0 1.0] (2nd row, last cell)"
    (is (= (char2offset \K) [4.0 1.0])))
  (testing "Z = [4.0 4.0] (5th and last row, 5th and last col)"
    (is (= (char2offset \Z) [4.0 4.0]))))
; (test-char2offset)

(deftest test-offset2topright
  (testing "A = major-origin (top right)"
    (is (= (offset2topright [0.0 0.0] major-origin major-cell-width) major-origin)))
  (testing "B = [-500000.0 2000000.0] (top row, second cell)"
    (is (= (offset2topright [1.0 0.0] major-origin major-cell-width) [-500000.0 2000000.0])))
  (testing "E = [1000000.0 2000000.0] (top row, 5th and last cell)"
    (is (= (offset2topright [4.0 0.0] major-origin major-cell-width) [1000000.0 2000000.0])))
  (testing "F = [-1000000.0 1500000.0] (2nd row, first cell, the coords wrap around)"
    (is (= (offset2topright [0.0 1.0] major-origin major-cell-width) [-1000000.0 1500000.0])))
  (testing "K = [1000000.0 1500000.0] (2nd row, last cell)"
    (is (= (offset2topright [4.0 1.0] major-origin major-cell-width) [1000000.0 1500000.0])))
  (testing "Z = [1000000.0 0.0] (5th and last row, 5th and last col)"
    (is (= (offset2topright [4.0 4.0] major-origin major-cell-width) [1000000.0 0.0]))))
; (test-offset2topright)

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
