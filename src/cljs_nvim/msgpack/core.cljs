(ns cljs-nvim.msgpack.core)

;; hack
(def msgpack (js/require "/usr/local/lib/node_modules/msgpack-lite"))

(defn decode [data]
  (.decode msgpack data))

(defn encode [data]
  (.encode msgpack (clj->js data)))

(defn create-encode-stream 
  ([] (.createEncodeStream msgpack))
  ([codec] (.createEncodeStream msgpack (clj->js {:codec codec}))))

(defn create-decode-stream 
  ([] (.createDecodeStream msgpack))
  ([codec] (.createDecodeStream msgpack (clj->js {:codec codec}))))

(defn create-codec 
  ([] (.createCodec msgpack))
  ([opts] (.createCodec msgpack (clj->js opts))))
