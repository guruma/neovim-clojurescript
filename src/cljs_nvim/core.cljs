(ns cljs-nvim.core
  (:require-macros [cljs-nvim.plugin :refer [defhandler]])
  (:require [cljs-nvim.node.process :as process]
            [cljs-nvim.msgpack.rpc :as rpc]
            [cljs-nvim.util :refer [log] :as u]
            [cljs-nvim.plugin :as plugin]))

;(nodejs/enable-util-print!)

(def my-plugin (plugin/make-plugin))

(defn attach-plugin []
  (plugin/attach my-plugin process/stdin process/stdout))


(defhandler 
  {:sync false :type "command" :name "NodeCmdArg0" :opts {:nargs 0}} 
  node-cmd-arg0 [plugin params]
   (log "node-cmd-arg0")
   (rpc/send-command plugin (str "echom 'Hello, NVIM'")))

(defhandler 
  {:sync false :type "command" :name "NodeCmdArg1" :opts {:nargs "1"}}
  node-cmd-arg1 [plugin params]
   (log "node-cmd-arg1")
   (rpc/send-command plugin (str "echom 'Hello, nvim! - " params "'")))
 
(defhandler 
  {:sync true :type "command" :name "NodeCmdArgN" :opts {:nargs "*"}} 
  node-cmd-argn [plugin params]
   (log "node-cmd-argn")
   (rpc/send-command plugin (str "echom 'HHello, nvim! - " params "'")))


(defn print-macro []
  (u/prints
    (macroexpand
      '(defhandler {:sync true :nargs 1 :name "NodeCmdArgN"} 
        node-cmd-argn [plugin params]
         (log "node-cmd-argn")
         (send-command (str "echom 'hello, nvim! - " params "'"))))))

(defn my-test []
  (u/prints (str "plugin path: " (u/plugin-path) "\n"))
  (u/prints (str "plugin handlers: " @plugin/handlers* "\n"))
  (u/prints (str "plugin specs: " (plugin/specs) "\n"))
  #_(print-macro))

(defn -main [& args]
  (log "main")
  (if process/tty?
    (my-test)
    (attach-plugin)))

(set! *main-cli-fn* -main)
