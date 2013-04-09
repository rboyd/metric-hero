(defproject metric-hero "0.1.1-SNAPSHOT"
  :description "Metric-hero analyzes your project and emits a navigable 3D visualization of it."
  :url "http://github.com/rboyd/metric-hero"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "0.3.0"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.2.1"]
                 [clj-jgit "0.2.1"]
                 [ring/ring-jetty-adapter "1.2.0-beta2"]
                 [compojure "1.1.5"]]
  :main metric-hero.core
  :cljsbuild {
    :builds [{
        ; The path to the top-level ClojureScript source directory:
        :source-paths ["src-cljs"]
        ; The standard ClojureScript compiler options:
        ; (See the ClojureScript compiler documentation for details.)
        :compiler {
          :output-to "resources/public/metric-hero.js"
          :optimizations :whitespace
          :pretty-print true}}]}) 
