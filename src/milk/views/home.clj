(ns milk.views.home
  (:require [noir.response :refer [redirect]]
            [compojure.core :refer [defroutes GET]]
            [milk.views.common :refer [show-layout show-markdown show-home show-git show-parsehttp]]))



(defroutes home-routes
  (GET "/milk"   [] (redirect "https://github.com/my-cat/milk"))
  (GET "/ghi"  [] (redirect "https://github.com/Raynes/refheap/issues"))
  (GET "/wiki" [] (redirect "https://github.com/Raynes/refheap/wiki"))
   (GET "/wiki1" [] (show-layout ))
   (GET "/markdown" [] (show-markdown))
  (GET "/paper" [] (show-home) )
  (GET "/python" [] (show-git) )
   (GET "/python/parsehttp" [] (show-parsehttp) ))