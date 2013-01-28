(ns treemapper.core
  (:require [clojure.data.json :as json])
  (:import java.io.File)
  (:import java.io.FileNotFoundException))

; hat tip to @bpsm (https://gist.github.com/613279)
(defn as-file [s]
  "Return whatever we have as a java.io.File object"
  (cond (instance? File s) s   ; already a file, return unchanged
        (string? s) (File. s)  ; return java.io.File for path s
        :else (throw (FileNotFoundException. (str s)))))

(defn walk [^File file]
  (let [node     {:name (.getName file)}
        children (map walk (.listFiles file))]
    (if (empty? children) (assoc node :size (.length file)) (assoc node :children children))))

(defn analyze [dir]
  (spit "output.json" (-> dir as-file walk json/write-str)))
