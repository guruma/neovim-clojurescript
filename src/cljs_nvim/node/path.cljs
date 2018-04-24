(ns cljs-nvim.node.path
  (:refer-clojure :exclude [resolve]))

(def path (js/require "path"))

(defn dirname [p]
  (.dirname path p))

(defn basename 
  ([p] (.basename path p))
  ([p ext] (.basename path p ext)))

(defn resolve [& args]
  (apply path.resolve args))

