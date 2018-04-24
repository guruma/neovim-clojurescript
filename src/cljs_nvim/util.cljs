(ns cljs-nvim.util
  (:require [cljs.nodejs :as nodejs]
            [cljs-nvim.node.fs :as fs]
            [cljs-nvim.node.path :as path]
            [cljs-nvim.node.process :as process]))

(defn plugin-path []
  js/__filename)

(defn plugin-dir []
  js/__dirname)

(defn plugin-file-name []
  (path/basename js/__filename))

(let [print-enabled# (atom false)]
  (defn prints [& args]
    (when-not @print-enabled#
      (nodejs/enable-util-print!)
      (reset! print-enabled# true))
    (when process/tty?
      (apply println args))))

(defn- log* [filename content]
  (fs/append-file-sync filename content))

(defn log [data]
  (log* "log.txt" 
        (str "[" (plugin-file-name) "] : " data "\n")))

