(defproject gridref "0.1.0"
  :description "OS GB Grid Reference to Britsh National Grid Easting / Northing"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [org.clojure/tools.trace "0.7.6"]]
  :main gridref.core
  :profiles {:uberjar {:aot :all}})
