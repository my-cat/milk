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


(defpartial post-fields [{:keys [content title ]}]
           
            
            (text-field {:placeholder "Title"} :title  title ) 
            (text-area {:placeholder "Body"} :content content))


(defpartial paste-item [{:keys [ perma-link title summary  date id] :as paste}]
            (when paste
              [:li.post
              [:h2 (link-to perma-link title)]
               [:ul.datetime
                [:li date]
                [:li id]
                ]
               [:div.content summary]]))

(defpartial paste-page [items]
            (main-layout
              [:ul.posts
               (map paste-item items)]))

(defn create-paste [{:keys [title content private]}]
  (let [paste (paste/paste  title content  "my name")]
    (if (map? paste)
      (redirect (str "/" ))
    ))) 


(defroutes paste-routes 
  (GET "/"   []  (paste-page  (paste/get-pastes 1)) )
	(GET "/milk/paste/add" {:as paste} 
		(admin-layout
           (form-to [:post "/milk/paste/add"]
                      [:ul.actions
                        [:li (link-to {:class "submit"} "/milk/paste/add" "Add")]]
                    (post-fields paste)
                    (submit-button {:class "submit"} "add post"))))

	(POST "/milk/paste/add" {:keys [params]}
          
          (create-paste params)))