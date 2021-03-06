(defproject milk "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-time "0.3.7"]
   				       [compojure "1.1.5"]
                 [lib-noir "0.7.0"]
                 [com.novemberain/monger "1.6.0-beta2"]
                 [hiccup-bridge "1.0.0-SNAPSHOT"]
                 [org.clojure/tools.macro "0.1.2"]
                 [hiccup "1.0.3"]
                 [me.raynes/cegdown "0.1.0"]
                 [me.raynes/laser "1.1.1"]
                 [ring-mock "0.1.5"]
                 [clout "1.1.0"]
                 [com.draines/postal "1.11.0"]
                 ]
  :plugins [[lein-ring "0.8.7"]]
  :ring { :handler milk.server/handler  :init milk.server/init!}
  :min-lein-version "2.0.0"
  )
