(ns metric-hero.core-test
  (:use expectations
        metric-hero.core))

; testing name-to-time
;   given a list of commit-infos, collect filenames and return {\"filename\" #latest-inst ...}
(let [later-time #inst "1982-02-15T08:00:02.333-00:00"
      earlier-time #inst "1982-02-05T08:00:02.333-00:00"]
  (expect {"filename" (.getTime later-time)
           "otherfile" (.getTime earlier-time)}
          (name-to-time [{:changed_files [["filename" :edit]] :time later-time}
                         {:changed_files [["filename" :edit] ["otherfile" :add]] :time earlier-time}])))
