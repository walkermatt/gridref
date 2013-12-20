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

(deftest test-pad-head
  (testing "Single digit"
    (is (= (pad-head 1) "00001")))
  (testing "Two digits"
    (is (= (pad-head 12) "00012")))
  (testing "Three digits"
    (is (= (pad-head 123) "00123")))
  (testing "Four digits"
    (is (= (pad-head 1234) "01234")))
  (testing "Five digits"
    (is (= (pad-head 12345) "12345"))))
; (test-pad-head)

;; Grid reference to coord

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

;; Coordinate to grid reference

(deftest test-cell2char
  (testing "0 = A" (is (= (cell2char 0) \A)))
  (testing "1 = B" (is (= (cell2char 1) \B)))
  (testing "2 = C" (is (= (cell2char 2) \C)))
  (testing "3 = D" (is (= (cell2char 3) \D)))
  (testing "4 = E" (is (= (cell2char 4) \E)))
  (testing "5 = F" (is (= (cell2char 5) \F)))
  (testing "6 = G" (is (= (cell2char 6) \G)))
  (testing "7 = H" (is (= (cell2char 7) \H)))
  (testing "I is skipped" (is (not (= (cell2char 8) \I))))
  (testing "8 = J" (is (= (cell2char 8) \J)))
  (testing "9 = K" (is (= (cell2char 9) \K)))
  (testing "24 = Z" (is (= (cell2char 24) \Z))))
; (test-cell2char)

(deftest test-offset2cell
  (testing "[0 0] = 0 (first col, first row)"
    (is (= (offset2cell [0.0 0.0]) 0.0)))
  (testing "[1 0] = 1"
    (is (= (offset2cell [1.0 0.0]) 1.0)))
  (testing "[2 0] = 2"
    (is (= (offset2cell [2.0 0.0]) 2.0)))
  (testing "[3 0] = 3"
    (is (= (offset2cell [3.0 0.0]) 3.0)))
  (testing "[4 0] = 4 (last col, first row)"
    (is (= (offset2cell [4.0 0.0]) 4.0)))
  (testing "[0 1] = 5 (First col, second row)"
    (is (= (offset2cell [0.0 1.0]) 5.0)))
  (testing "[4 4] = 24 (Last col, last row)"
    (is (= (offset2cell [4.0 4.0]) 24.0))))
; (test-offset2cell)

(deftest test-coord2offset
  (testing "A = top left major cell"
    (is (= (coord2offset [-1000000 1500000] major-origin major-cell-width) [0.0 0.0])))
  (testing "E = top row, 5th and last major cell"
    (is (= (coord2offset [1000000 1500000] major-origin major-cell-width) [4.0 0.0])))
  (testing "F = 2nd row, first major cell, the coords wrap around)"
    (is (= (coord2offset [-1000000 1000000] major-origin major-cell-width) [0.0 1.0])))
  (testing "K = 2nd row, last major cell"
    (is (= (coord2offset [1000000 1000000] major-origin major-cell-width) [4.0 1.0])))
  (testing "S = 4th row, 3rd col major cell"
    (is (= (coord2offset [0 0] major-origin major-cell-width) [2.0 3.0])))
  (testing "Z = bottom right major cell"
    (is (= (coord2offset [1000000 -500000] major-origin major-cell-width) [4.0 4.0]))))
; (test-coord2offset)

(deftest test-offset2cell
  (testing "A" (is (= (offset2cell [0.0 0.0]) 0.0)))
  (testing "B" (is (= (offset2cell [1.0 0.0]) 1.0)))
  (testing "E" (is (= (offset2cell [4.0 0.0]) 4.0)))
  (testing "F (wraps to next row)" (is (= (offset2cell [0.0 1.0]) 5.0)))
  (testing "Z" (is (= (offset2cell [4.0 4.0]) 24.0))))
; (test-offset2cell)

(deftest test-coord2alpha
  (testing "AA" (is (= (coord2alpha [-1000000 1900000]) "AA")))
  (testing "AV" (is (= (coord2alpha [-1000000 1500000]) "AV")))
  (testing "AZ" (is (= (coord2alpha [-600000 1500000]) "AZ")))
  (testing "ZZ" (is (= (coord2alpha [1400000 -500000]) "ZZ")))
  (testing "SO8584975045" (is (= (coord2alpha [385849 275045]) "SO")))
  (testing "SP0614186848" (is (= (coord2alpha [406141 286848]) "SP")))
  (testing "NT2499574112" (is (= (coord2alpha [324995 674112]) "NT")))
  (testing "TQ3007480274" (is (= (coord2alpha [530074 180274]) "TQ")))
  (testing "SV9178010372" (is (= (coord2alpha [91780.0 10372.0]) "SV")))
  (testing "HP6322316714" (is (= (coord2alpha [463223.0 1216714.0]) "HP"))))
; (test-coord2alpha)

(deftest test-coord2ref
  (testing "SO8584975045" (is (= (coord2ref [385849 275045]) "SO8584975045")))
  (testing "SP0614186848, leading 0" (is (= (coord2ref [406141 286848]) "SP0614186848")))
  (testing "NT2499574112" (is (= (coord2ref [324995 674112]) "NT2499574112")))
  (testing "TQ3007480274" (is (= (coord2ref [530074 180274]) "TQ3007480274")))
  (testing "SV9178010372" (is (= (coord2ref [91780.0 10372.0]) "SV9178010372")))
  (testing "HP6322316714" (is (= (coord2ref [463223.0 1216714.0]) "HP6322316714"))))
; (test-coord2ref)

(deftest test-convert
  (testing "Just letters are acceptable" (is (not (= (convert ["SO"]) nil) )))
  (testing "Letters and numbers are acceptable" (is (not (= (convert ["SO12"]) nil) )))
  (testing "Lowercase letters are acceptable" (is (not (= (convert ["so"]) nil) )))
  (testing "Spaces in grid refs are acceptable" (is (not (= (convert ["SO 12 34"]) nil) )))
  (testing "Coords with square brackets are acceptable" (is (not (= (convert ["[123456 654321]"]) nil) )))
  (testing "Coords without square brackets are acceptable" (is (not (= (convert ["123456 654321"]) nil) )))
  (testing "Coords with decimals are acceptable" (is (not (= (convert ["123456.00 654321.00"]) nil) ))))
(test-convert)
