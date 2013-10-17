(ns milk.views.admin
(:use 
        hiccup.core
        hiccup.element
        hiccup.form)
  (:require [noir.session :as session]
            [noir.validation :as vali]
            [noir.response :as resp]
            [clojure.string :as string]
            [milk.models.paste :as pastes]
            [milk.models.user :as users]
            [compojure.core :refer [defroutes GET POST]]
            [milk.views.common :refer [main-layout admin-layout defpartial]] ))

;; Links


(def post-actions [{:url "/milk/paste/add" :text "Add a post"}])
(def user-actions [{:url "/blog/admin/user/add" :text "Add a user"}])

;; Partials

(defpartial error-text [errors]
            [:span (string/join "" errors)])

(defpartial post-fields [{:keys [title contents]}]
            (vali/on-error :title error-text)
            (text-field {:placeholder "Title"} :title title)
            (vali/on-error :contents error-text)
            (text-area {:placeholder "Body"} :contents contents))

(defpartial user-fields [{:keys [username] :as usr}]
            (vali/on-error :username error-text)
            (text-field {:placeholder "Username"} :username username)
            (vali/on-error :password error-text)
            (password-field {:placeholder "Password"} :password))


(defpartial action-item [{:keys [url text]}]
            [:li
             (link-to url text)])

(defpartial user-item [{:keys [username]}]
            [:li
             (link-to (str "/milk/admin/user/edit/" username) username)])

(defpartial post-item [{:keys [title] :as paste}]
            [:li
             (link-to (pastes/edit-url paste) title)])


(defn login [params ] 
    (if (users/admin?)
           (resp/redirect "/milk/admin")
           (main-layout
             (form-to [:post "/milk/login"]
                      [:ul.actions
                       [:li (link-to {:class "submit"} "/" "Login")]]
                      (user-fields params)
                      (submit-button {:class "submit"} "submit")))))


(defn milk-admin  []
    (if  (nil?  (users/admin?))
             (resp/redirect "/milk/login")
             (admin-layout   
                [:ul.actions
            (map action-item post-actions)]
           [:ul.items
            (map post-item (pastes/get-latest))])
                ))
    

(defn paste-user [{:keys [username password] :as user}]
      (if (users/login! {:username (user "username") :password (user "password")})
           (resp/redirect "/milk/admin")
           (main-layout
             (form-to [:post "/milk/login"]
                      [:ul.actions
                       [:li (link-to {:class "submit"} "/" "Login")]]
                      (user-fields user)
                      (submit-button {:class "submit"} "submit")))))

(defn logout [] 
    (println "aaa")
    (session/clear!) 
    (resp/redirect "/"))

(defroutes user-routes 
  (GET "/milk/admin"   [] (milk-admin) 
      )
  (GET "/milk/login" {:keys [params]} 
          (login params)
    )
  (POST "/milk/login"  {:keys [form-params] :as request }
    (paste-user form-params ))

  (GET "/milk/logout" [] 
    (logout ))
  )
 