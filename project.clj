(defproject milk "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
   				[compojure "1.1.5"]
                [lib-noir "0.5.6"]
                [com.novemberain/monger "1.6.0-beta2"]
                [hiccup-bridge "1.0.0-SNAPSHOT"]]
  :plugins [[lein-ring "0.8.2"]]
  :ring {:handler refheap.server/handler})
