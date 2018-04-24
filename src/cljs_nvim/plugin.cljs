(ns cljs-nvim.plugin
  (:require [cljs-nvim.msgpack.core :as msgpack]
            [cljs-nvim.msgpack.rpc :as rpc]
            [cljs-nvim.util :refer [log] :as u]))

(def ^:private handlers* (atom {}))

(defn specs []
  (log "specs() called.")
  (map second (vals @handlers*)))

(defn get-handler [{:keys [handlers]} method-name]
  (if-let [v (@handlers method-name)]
    v
    (log (str "Sorry. In plugin there is no handler for " method-name))))

(defn add-handler [command handler spec]
  (swap! handlers*
         assoc 
         (str (u/plugin-path) ":command:" command)
         [handler spec]))

; request: [type, msgid, method, params]
(defn- handle-request [plugin [_ msgid method params]]
  (log "handle-request:")
  (log (str "  [type:]  " _))
  (log (str "  [msgid:] " msgid))
  (log (str "  [method] " method))
  (log (str "  [params] " params))

  (cond
    (= method  "poll") 
    (do 
      (log "handle-poll")
      (rpc/send-ok plugin msgid))

    (= method "specs")
    (do
      (log "handle-specs")
      (rpc/send-response plugin
                         msgid 
                         nil 
                         (specs)))

    (get-handler plugin method)
    (let [[handler-fn spec] (get-handler plugin method)]
      (if (fn? handler-fn)
        (let [result (handler-fn plugin params)]
          ;
          ; https://neovim.io/doc/user/remote_plugin.html
          ;
          ; when sync flag is true, nvim call 'rpcrequest' which makes 
          ; it block until it receives the response.
          ; when sync is false, nvim call 'rpcnotify' which make it use 
          ; 'fire and forget' approach, meaning return values or 
          ; exceptions raised in the handler function of the client 
          ; are ignored.
          ;
          ; so...and here, I decided to response when sync is true.
          ;
          (when (:sync spec) (rpc/send-response plugin msgid nil result)))
        (log (str "The handler is not a function : " method))))

    :else 
      (log "handle-invalid-request")))


; notification: [type, method, params]
(defn- handle-notification [plugin [_ method params]]
  (log (str "handle-notification: " method))
  (log (str "  [type:]  " _))
  (log (str "  [method] " method))
  (log (str "  [params] " params))

  (if-let [[handler-fn _] (get-handler plugin method)]
    (handler-fn plugin params)
    (log "handle-invalid-notification")))

; response: [type, msgid, error, result]
(defn- handle-response [plugin [_ msgid error params]]
  (log (str "handle-response: "))
  (log (str "  [type:] " _))
  (log (str "  [msgid] " msgid))
  (log (str "  [error] " error))
  (log (str "  [params] " params)))


;; ***** Public *****

(defn attach 
  [{:keys [input-stream output-stream
           encode-stream decode-stream]
    :as plugin}
   input output]

  (reset! input-stream input)
  (reset! output-stream output)
  (.pipe encode-stream @output-stream)
  (.on 
    (.pipe @input-stream decode-stream)
    "data" 
    (fn [[type & _ :as data]]
      (condp = type
        rpc/*request*      (handle-request plugin data)
        rpc/*notification* (handle-notification plugin data)
        rpc/*response*     (handle-response plugin data)
        (log "invalid type of msgpack protocol")))))

(defn dettach 
  [{:keys [input-stream output-stream
           encode-stream decode-stream
           handle-data-fn]}]

  (.unpipe @input-stream decode-stream)
  (.unpipe encode-stream @output-stream)
    #_(.end encode-stream))  ; TODO: end를 호출해야 하나?


(defn make-plugin []
 {:handlers handlers*
  :sending-messages (atom {})
  :encode-stream (msgpack/create-encode-stream) 
  :decode-stream (msgpack/create-decode-stream)
  :input-stream (atom nil)
  :output-stream (atom nil)})
