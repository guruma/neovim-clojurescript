(ns cljs-nvim.msgpack.rpc
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<! >!] :as async]
            [cljs-nvim.util :refer [log]]))

(def *request* 0)
(def *response* 1)
(def *notification* 2)


(let [id (atom 1)]
  (defn gen-id []
    (swap! id inc)))

#_(defn send-data-async! [plugin [_ msgid _ _ :as data] callback-fn]
  (log (str "send-data-async: " data))
  (let [enc (:encode-stream plugin)
             sending-messages (:sending-messages plugin)]
    (if (fn? callback-fn)
      (swap! sending-messages assoc msgid callback-fn))
    (.write enc (clj->js data))
    (._flush enc)))

#_(defn send-data [plugin data]
  (log (str "send-data: " data))
  (let [p (promise)]
    (send-data-async! plugin data #(deliver p %))
    @p))

(defn send-data [plugin data]
  (log (str "send-data: " data))
  (when-let [enc (:encode-stream plugin)]
    (.write enc (clj->js data))
    (._flush enc)))

(defn send-request [plugin & data]
  (log (str "send-request: " data))
  (send-data plugin (concat [*request* (gen-id)] data)))

(defn send-response [plugin & data]
  (log (str "send-response: " data))
  (send-data plugin (concat [*response*] data)))

(defn send-ok [plugin msgid]
  (log (str "send-ok: " msgid))
  (send-response plugin msgid nil "ok"))

(defn send-command [plugin & data]
  (send-request plugin "nvim_command" data))
