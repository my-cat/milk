(ns milk.views.paste
	(:use 
       hiccup.core
       hiccup.page
       hiccup.element
       hiccup.form)
  (:require [milk.models.paste :as paste]
            [noir.session :as session]
            [noir.response :refer [redirect content-type]]
            [clojure.java.io :refer [resource]]
            [compojure.core :refer [defroutes GET POST]]
            [milk.views.common :refer [main-layout admin-layout defpartial]]
            [clojure.string :refer [split join]]))


(defpartial post-fields [{:keys [private content title ]}]
           
            (drop-down :private  [["私有" true] ["公有" false]]) 
            
            (text-field {:placeholder "Title"} :title  title ) 
            (text-area {:placeholder "Body"} :content content))



(defn create-paste [{:keys [title content private]}]
  (let [paste (paste/paste  title content private  "my name")]
    (if (map? paste)
      (redirect (str "/milk/paste/add" ))
    ))) 


(defroutes paste-routes 
	(GET "/milk/paste/add" {:as paste} 
		(admin-layout
           (form-to [:post "/milk/paste/add"]
                      [:ul.actions
                        [:li (link-to {:class "submit"} "/milk/paste/add" "Add")]]
                    (post-fields paste)
                    (submit-button {:class "submit"} "add post"))))

	(POST "/milk/paste/add" {:keys [params]}
          
          (create-paste params)))