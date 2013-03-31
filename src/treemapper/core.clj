(ns treemapper.core
  (:gen-class)
  (:use compojure.core)
  (:use ring.adapter.jetty)
  (:require [clojure.data.json :as json]
            [clojure.set :refer [rename-keys]]
            [clj-jgit.porcelain :refer [load-repo with-repo git-log]]
            [clj-jgit.querying :refer [rev-list commit-info]]
            [compojure.route :as route]
            [ring.util.response :as resp])
  (:import java.io.File)
  (:import java.io.FileNotFoundException))

; hat tip to @bpsm (https://gist.github.com/613279)
(defn as-file [s]
  "Return whatever we have as a java.io.File object"
  (cond (instance? File s) s   ; already a file, return unchanged
        (string? s) (File. s)  ; return java.io.File for path s
        :else (throw (FileNotFoundException. (str s)))))

(defn filter-files [^File dir ignore]
  (for [file (.listFiles dir)
        :let [name (.getName file)]
        :when (not-any? #(re-find % name) ignore)]
    file))

(defn walk [^File file file-time-map & {:keys [ignore]}]
  (let [path     (.getPath file)
        node     {:name path}
        children (map #(walk % file-time-map :ignore ignore) (filter-files file ignore))]
    (-> (if (empty? children) (assoc node :size (.length file)) (assoc node :children children))
        (assoc :modified (if (contains? file-time-map path)
                           (file-time-map path)
                           0)))))

(defn name-to-time [commits]
  (->> (for [commit commits
        :let [files (map first (:changed_files commit))
              time  (.getTime (:time commit))]]
         (mapcat vector files (repeat time)))
       (apply concat)
       (partition 2)
       (map vec)
       reverse
       (into {})))

(defn commits-for [path]
  (with-repo path
    (map #(commit-info repo %1) (rev-list repo))))

(defn prepend-keys
  "Prepends the string to every key in the map"
  [x kmap]
  (let [mapkeys (keys kmap)
        renamed (into {} (map #(identity [% (str x %)]) mapkeys))]
    (rename-keys kmap renamed)))

(defn analyze [dir & {:keys [ignore]}]
  (let [dir-slash     (if (= (last dir) \/) dir (str dir \/))
        file-time-map (->> (commits-for dir)
                           name-to-time
                           (prepend-keys dir-slash))]
    (spit "output.json" (-> dir as-file (walk file-time-map :ignore ignore) json/write-str))))

(defroutes app
  (GET "/" [] (resp/file-response "index.html" {:root "resources/public"}))
  (route/resources "/"))

(defn -main
  [& args]
  (run-jetty app {:port 3000}))
