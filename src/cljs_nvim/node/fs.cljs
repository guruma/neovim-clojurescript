(ns cljs-nvim.node.fs)

(def fs (js/require "fs"))

(defn append-file-sync [filename content]
  (.appendFileSync fs filename content "utf8"))

(defn read-file-sync [path]
  (.readFileSync fs path))

(defn write-file-sync [fd buf & [offset length position]]
  (.writeFileSync fs fd buf offset length position))

(defn copy-file-sync [src dst]
  (.copyFileSync fs src dst))

(defn mk-dir-sync 
  ([path  ] (.mkdirSync fs path))
  ([path  mode] (.mkdirSync fs path mode)))

(defn exists-sync [path]
  (.existsSync fs path))

