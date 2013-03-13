(ns treemapper.core-test
  (:use clojure.test
        treemapper.core))

(deftest name-to-time-test
  (testing "given a list of commit-infos, collect filenames and return {\"filename\" #latest-inst ...}"
    (let [later-time #inst "1982-02-15T08:00:02.333-00:00"
          earlier-time #inst "1982-02-05T08:00:02.333-00:00"]
    (is (= (name-to-time [{:changed_files [["filename" :edit]] :time later-time}
                          {:changed_files [["filename" :edit] ["otherfile" :add]] :time earlier-time}])
           {"filename" (.getTime later-time)
            "otherfile" (.getTime earlier-time)})))))
