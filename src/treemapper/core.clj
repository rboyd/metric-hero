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

(defn filter-files [^File file ignore]
  (let [files        (.listFiles file)
        regex-filter (fn [fname]
                       (not-any? #(re-find % (.getName fname)) ignore))]
    (filter regex-filter files)))

(defn walk [^File file & {:keys [ignore]}]
  (let [node     {:name (.getName file)}
        children (map walk (filter-files file ignore))]
    (-> (if (empty? children) (assoc node :size (.length file)) (assoc node :children children))
        (assoc :modified (.lastModified file)))))

(defn analyze [dir & {:keys [ignore]}]
  (spit "output.json" (-> dir as-file (walk :ignore ignore) json/write-str)))
