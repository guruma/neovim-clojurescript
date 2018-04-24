(ns cljs-nvim.node.child-process)

(def cp (js/require "child_process"))

(defn spawn-sync 
  ([cmd] (.spawnSync cp cmd))
  ([cmd args] (.spawnSync cp cmd (clj->js args)))
  ([cmd args opts] (.spawnSync cp cmd (clj->js args) (clj->js opts))))

(defn spawn
  ([cmd] (.spawn cp cmd))
  ([cmd args] (.spawn cp cmd (clj->js args)))
  ([cmd args opts] (.spawn cp cmd (clj->js args) (clj->js opts))))
