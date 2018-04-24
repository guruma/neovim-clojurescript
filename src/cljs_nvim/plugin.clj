(ns cljs-nvim.plugin
  (:require [cljs.analyzer.api :as ana]))


(defn get-val [spec kw]
  (if-let [spec (cond 
                  (map? spec) spec
                  (symbol? spec) (first `(~kw ~spec))
                  :else (throw (Exception. "spec isn't a map.")))]
     (if (contains? spec kw)
       (kw spec)
       (throw (Exception. (str "spec hasn't the key :" (name kw) "."))))))

(defmacro defhandler [spec handler args & body]
  (let [command (get-val spec :name)]
    `(do (defn ~handler ~args ~@body)
       (cljs-nvim.plugin/add-handler ~command ~handler ~spec))))
