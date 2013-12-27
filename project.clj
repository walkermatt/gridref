(defproject gridref "0.1.0"
  :description "Convert an alpha numeric Ordnance Survey grid reference to easting / northing or easting / northing to a grid reference."
  :url "https://github.com/walkermatt/gridref"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [org.clojure/tools.trace "0.7.6"]
                 [org.clojure/tools.cli "0.3.0"]]
  :main gridref.core
  :profiles {:uberjar {:aot :all}})
