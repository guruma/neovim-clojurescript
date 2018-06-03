(ns cljs-nvim.msgpack.rpc
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [<! >!] :as async]
            [cljs-nvim.util :refer [log]]))

(def deasync (js/require "/usr/local/lib/node_modules/deasync"))

(def *request* 0)
(def *response* 1)
(def *notification* 2)


(let [id (atom 1)]
  (defn gen-id []
    (swap! id inc)))

(defn attached? 
  [{:keys [input-stream output-stream]}]
  (and @input-stream @output-stream))

(defn send-data 
  [{:keys [encode-stream] :as plugin} data]
  (log (str "send-data: " data))
  (when (attached? plugin)
    (.write encode-stream (clj->js data))
    (._flush encode-stream)))

(defn send-data-sync 
  [{:keys [responses] :as plugin}
   [_ msgid _ _ :as data]]
  (log (str "send-data-sync: " data))
  (when (attached? plugin)
    (swap! responses assoc msgid nil)
    (send-data plugin data)
    (loop []
      (log (str "send-data-sync: waiting response..."))
      (deasync.sleep 500)
      (if-let [ret (get @responses msgid)]
        (do (swap! responses dissoc msgid)
          (log (str "send-data-sync: got response..." ret))
          ret)
        (recur)))))

(defn send-request [plugin & data]
  (log (str "send-request: " data))
  (send-data plugin (concat [*request* (gen-id)] data)))

(defn send-request-sync [plugin & data]
  (log (str "send-request-sync: " data))
  (let [r (send-data-sync plugin (concat [*request* (gen-id)] data))]
    (log (str "send-request-sync received: " r))
    r))

(defn send-response [plugin & data]
  (log (str "send-response: " data))
  (send-data plugin (concat [*response*] data)))

(defn send-ok 
  ([plugin] (send-ok plugin (:msgid plugin)))
  ([plugin msgid]
    (log (str "send-ok: " msgid))
    (send-response plugin msgid nil "ok")))

(defn send-command [plugin & data]
  ;(send-request plugin "nvim_command" data))
  (send-request-sync plugin "nvim_command" data))
