(ns milk.server
  (:require 
            [noir.util.middleware :refer [wrap-strip-trailing-slash wrap-canonical-host wrap-force-ssl]]
            [noir.session :refer [wrap-noir-session wrap-noir-flash]]
            
            [noir.validation :as vali]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.ring.session-store :refer [monger-store]]
            [compojure.core :refer [defroutes routes ANY]]
            [compojure.handler :refer [api]]
            [compojure.route :refer [not-found resources]]
            [monger.query :refer [with-collection find sort limit paginate]]
            [milk.views.common :refer [main-layout]]
       [milk.views.home :refer [home-routes]]
   [milk.views.paste :refer [paste-routes]]
   [milk.views.admin :refer [user-routes]]
   [milk.models.paste :as pastes]
           
            ))



(defn init! []
(let [uri (get (System/getenv) "MONGOLAB_URI" "mongodb://xiaomuei:lawe3413@dharma.mongohq.com:10020/milk-development")]
  (mg/connect-via-uri! uri )
  (mc/ensure-index "pastes" {:user 1 :date 1})
(mc/ensure-index "pastes" {:private 1})
(mc/ensure-index "pastes" {:id 1})
(mc/ensure-index "pastes" {:paste-id 1})
(mc/ensure-index "pastes" {:fork 1})
 ))




(def initialized (ref nil))

(defn initialize [handler]
  (fn [request]
    (when (not @initialized)
      (dosync (init!) (ref-set initialized true)))
    (handler request)))






(defn four-zero-four []
  (main-layout
  [:p  "inset a  picture here "] ))

(defn wrap-prod-middleware [routes]
  (if (System/getenv "LEIN_NO_DEV")
    (-> routes
        (wrap-canonical-host (System/getenv "CANONICAL_HOST"))
        (wrap-force-ssl))
    routes))

(def handler
  (-> (routes 
      home-routes
      paste-routes
      user-routes
      (resources "/")
     )
      (api)
      (wrap-noir-flash)
      (wrap-noir-session )
      (wrap-strip-trailing-slash)
      (vali/wrap-noir-validation)
      (pastes/wrap-paste-id)

      ))