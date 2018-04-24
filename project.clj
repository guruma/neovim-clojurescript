(defproject test "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.async "0.4.474"]
                 [com.cemerick/piggieback "0.1.3"]]

  :plugins [[lein-cljsbuild "1.1.5"]]
  ;:repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild {
    :builds [{:id "prod"
              :source-paths ["src"]
              :compiler {
                :main cljs-nvim.core
                ; TODO: automatate this update.
                ; because test/integration.cljs refer to this output-to file
                ; if this is changed, test/integration.cljs file must be updated.
                :output-to "out/host-plugin.js"
                :target :nodejs
                :externs ["externs.js"]
                :pretty-print true
                ;:parallel-build true
                :watch-fn (fn [] (println "Updated build"))
                :optimizations :simple}
             }
             {:id "test"
              :source-paths ["src" "test"]
              :compiler {
                :main test.integration
                :output-to "out/integration-test.js"
                :target :nodejs
                :pretty-print true
                :optimizations :simple}
             }
            ]
    })
