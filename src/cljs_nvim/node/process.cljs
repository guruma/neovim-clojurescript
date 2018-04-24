(ns cljs-nvim.node.process)

(def tty?  (.-isTTY js/process.stdout))

(def stdin js/process.stdin)
(def stdout js/process.stdout)

(def home-dir (if (= js/process.platform "win32")
                js/process.env.HOMEPATH
                js/process.env.HOME))

(defn set-raw-mode [] (if js/process.stdin.setRawMode
    (.setRawMode js/process.stdin true)))

(defn exit []
  (.exit js/process))

(defn chdir [dir]
  (.chdir js/process dir))


