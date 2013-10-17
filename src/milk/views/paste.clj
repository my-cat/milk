(ns milk.views.paste
	(:use 
       hiccup.core
       hiccup.page
       hiccup.element
       hiccup.form)
  
  (:require [milk.models.paste :as pastes]
    [milk.models.user :as users]
             [noir.validation :as vali]
            [noir.session :as session]
            [noir.response :refer [redirect content-type]]
            [clojure.java.io :refer [resource]]
             [clojure.string :as string]
            [compojure.core :refer [defroutes GET POST]]
            [milk.views.common :refer [main-layout admin-layout defpartial]]
            [clojure.string :refer [split join]]))

(defpartial error-text [errors]
            [:span (string/join  errors)])

(defpartial post-fields [{:keys [contents title ]}]         
            (vali/on-error :title error-text)
            (text-field {:placeholder "Title" } :title  title )
            (vali/on-error :contents error-text) 
            (text-area {:placeholder "Body"} :contents contents))

(defpartial paste-item [{:keys [ perma-link title summary  date id] :as paste}]
            (when paste
              [:li.post
              [:h2 (link-to perma-link title)]
               [:ul.datetime
                [:li date]
                [:li id]
                [:li (link-to {:class "submit"} (str "/milk/paste/edit/" id) "edit")]
                ]
               [:div.content summary]]))

(defpartial paste-page [items]
            (main-layout
              [:ul.posts
               (map paste-item items)]))

(defpartial add-form  [paste ] 
   (form-to [:post "/milk/paste/add"]
                      [:ul.actions
                        [:li (link-to {:class "submit"} "/" "Add")]]
                    (post-fields paste)
                    (submit-button {:class "submit"} "add user")))

(defpartial delete-form [{:keys [id]}]
  (if-let [paste (pastes/get-paste-by-id id)] 
     (form-to [:post (str "/blog/admin/post/edit/" id)]
                        [:ul.actions
                        [:li (link-to {:class "submit"} "/" "Submit")]
                        [:li (link-to {:class "delete"} (str "/blog/admin/post/remove/" id) "Remove")]]
                    
                      (submit-button {:class "submit"} "submit"))
  ))

(defn create-paste [{:keys [title contents private]}]
  (println (users/me))
  (let [paste (pastes/paste  title contents  (users/me))]
    (if (map? paste)
      (redirect (str "/" ))
      (admin-layout
           (add-form paste))
    ))) 

(defn show [id]   
;(println "aaaaaaaaaaaaaaaa" (pastes/get-paste-by-id id) id)
   (if-let [paste (pastes/get-paste-by-id id)]
           (admin-layout
             (form-to [:post (str "/milk/paste/edit/" id)]
                      [:ul.actions
                        [:li (link-to {:class "submit"} "/" "Submit")]
                        [:li (link-to {:class "delete"} (str "/milk/paste/remove/" id) "Remove")]]
                      (post-fields paste)
                      (submit-button {:class "submit"} "submit"))))
  )

(defn  update-form [paste] 
     (admin-layout
             (form-to [:post (str "/milk/paste/edit/" (:id paste ))]
                      [:ul.actions
                        [:li (link-to {:class "submit"} "/" "Submit")]
                        [:li (link-to {:class "delete"} (str "/milk/paste/remove/" (:id paste )) "Remove")]]
                      (post-fields paste)
                      (submit-button {:class "submit"} "submit"))))

(defn update  [ {:keys [id title contents] :as paste} ]
;  (println "update" id title contents  paste (users/me) )
  (if (pastes/update-paste paste) 
        (redirect "/milk/admin")
        (update-form paste)
        )
  
  )
(defn remove-paste [id ] 
   (pastes/delete-paste id)
   (redirect "/milk/admin") )

(defn show-view [id] 
  ">_ <!!!!!")

(defroutes paste-routes 
  (GET "/"   []  (paste-page  (pastes/get-pastes 1)) )

	(GET "/milk/paste/add" {:as paste} 
		(admin-layout
           (form-to [:post "/milk/paste/add"]
                      [:ul.actions
                        [:li (link-to {:class "submit"} "/" "Add")]]
                    (post-fields paste)
                    (submit-button {:class "submit"} "add user"))))

	(POST "/milk/paste/add" {:keys [params]}          
          (create-paste params))

  (GET "/milk/paste/edit/:id" [id] 
          (show id))

  (POST "/milk/paste/edit/:id" {:keys [params]}  
          (update  params  ))

  (GET "/milk/paste/remove/:id" [id] 
        (remove-paste id ))

  (GET "/milk/view/:id" [id] 
         (show-view id))
  )



