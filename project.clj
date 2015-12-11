(defproject refunc "0.1.0-SNAPSHOT"
  :description "React stateless functional components for ClojureScript."
  :url "http://hraberg.github.io/refunc/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0-RC3"]
                 [org.clojure/clojurescript "1.7.189" :scope "provided"]
                 [cljsjs/react "0.14.3-0"]
                 [cljsjs/react-dom "0.14.3-1"]]

  :npm {:dependencies [[todomvc-common "^1.0.2"]
                       [todomvc-app-css "^2.0.3"]]
        :root "examples/todomvc/resources/public"}

  :plugins [[lein-npm "0.6.1"]
            [lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"
             :exclusions [org.clojure/clojure
                          ring/ring-core
                          commons-fileupload
                          clj-time
                          org.clojure/tools.reader]]]

  :profiles {:dev {:resource-paths ["examples/todomvc/resources"]
                   :dependencies [[com.cemerick/piggieback "0.2.1"
                                   :exclude [org.clojure/clojurescript]]
                                  [org.clojure/tools.nrepl "0.2.12"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :pedantic? :abort

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"  "examples/todomvc/src"]
                        :figwheel {:css-dirs ["examples/todomvc/resources/public/node_modules/todomvc-common"
                                              "examples/todomvc/resources/public/node_modules/todomvc-app-css"]}
                        :compiler {:main todomvc.core
                                   :asset-path "js/compiled/out"
                                   :output-to "examples/todomvc/resources/public/js/compiled/todomvc.js"
                                   :output-dir "examples/todomvc/resources/public/js/compiled/out"
                                   :source-map-timestamp true }}]}

  :figwheel {:nrepl-port 7888
             :nrepl-middleware [cider.nrepl/cider-middleware
                                cemerick.piggieback/wrap-cljs-repl]})
