(ns test.integration
  (:require [cljs-nvim.node.child-process :as cp]
            [cljs-nvim.node.process :as ps]
            [cljs-nvim.node.fs :as fs]
            [cljs-nvim.plugin :as plugin]
            [cljs-nvim.msgpack.rpc :as rpc]
            [cljs-nvim.util :refer [log] :as u]
            ))

;; TODO: To globally require for any platform. 
(def deasync (js/require "/usr/local/lib/node_modules/deasync"))

(def plugin-file-name "host-plugin.js")
(def plugin-dir (u/plugin-dir))
(def copy-plugin-dir (str plugin-dir "/rplugin/node"))
(def copy-plugin-path (str copy-plugin-dir "/" plugin-file-name))
(def nvimrc-file-name "nvimrc")
(def nvimrc-path (str plugin-dir "/" nvimrc-file-name))
(def rplugin-path (str ps/home-dir "/.local/share/nvim/rplugin.vim"))

(defn make-nvimrc []
  (console.log nvimrc-path)
  (when-not (fs/exists-sync nvimrc-path)
    (fs/write-file-sync nvimrc-file-name (str "set rtp+=" plugin-dir))))

(defn copy-plugin []
  (when-not (fs/exists-sync copy-plugin-dir)
    (cp/spawn-sync "mkdir" ["-p" copy-plugin-dir]))
  ; force to copy the updated file...
  (cp/spawn-sync "cp" [plugin-file-name copy-plugin-path]))

(defn test-rplugin-vim []
    (<= 0 (.indexOf (fs/read-file-sync rplugin-path) copy-plugin-path)))

(defn update-rplugins []
  (let [args ["-u" nvimrc-path
              "--headless" 
              "-i" "NONE" 
              "-N" 
              "-c" "UpdateRemotePlugins"
              "-c" "q!"]]
    (log "test update-rplugins")
    (make-nvimrc)
    (copy-plugin)
    (cp/spawn-sync "nvim" args)
    (console.log "cat ~/.local/share/nvim/rplugin.vim to check the updated rplugins.")
    ))

(defn test-nvim []
  (let [args ["-u" nvimrc-path
              "--embed" 
              "-i" "NONE" 
              "-N"
              ;"-c" "call remote#host#RegisterNodePlugin()"
              ]
        my-plugin (plugin/make-plugin)
        nvim (cp/spawn "nvim" args {})]
    (log "test test-nvim")
    (plugin/attach my-plugin (.-stdout nvim) (.-stdin nvim))
    ;(rpc/send-command my-plugin "echo 'hello'")
    (rpc/send-command my-plugin "NodeCmdArg1 222")
    ; NOTE: nvim exits without response to nvim command of "q!"
    ;(rpc/send-command my-plugin "q!")
    ))

(defn -main [& args]
  (update-rplugins)
  (if (test-rplugin-vim)
    (console.log "success to UpdateRemotePlugins...")
    (do (console.log "fail to UpdateRemotePlugins...")
      (ps/exit)))
  (test-nvim))

(set! *main-cli-fn* -main)

