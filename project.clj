(defproject treemapper "0.1.1-SNAPSHOT"
  :description "Given a directory, treemapper outputs a JSON file in the format expected by the d3 Treemap Layout"
  :url "http://github.com/rboyd/treemapper"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "0.3.0"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.2.1"]
                 [clj-jgit "0.2.1"]]
  :cljsbuild {
    :builds [{
        ; The path to the top-level ClojureScript source directory:
        :source-paths ["src-cljs"]
        ; The standard ClojureScript compiler options:
        ; (See the ClojureScript compiler documentation for details.)
        :compiler {
          :output-to "resources/treemapper.js"
          :optimizations :whitespace
          :pretty-print true}}]}) 
