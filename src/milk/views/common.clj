(ns milk.views.common
  (:use 
       hiccup.core
       hiccup.page
       hiccup.element)
(:require [clojure.string :as string]
            [clojure.tools.macro :as macro]
             [me.raynes.laser :refer [defdocument defragment] :as l]
             [hiccup-bridge.core :as hicv]
             [clojure.java.io :refer [resource]] ))
(defmacro defpartial
  "Create a function that returns html using hiccup. The function is callable with the given name. Can optionally include a docstring or metadata map, like a normal function declaration."
  [fname & args]
  (let [[fname args] (macro/name-with-attributes fname args)
        [params & body] args]
    `(defn ~fname ~params
       (html
        ~@body))))
(def main-links [{:url "/blog/admin" :text "Admin"}])

(def admin-links [{:url "/blog/" :text "Blog"}
                  {:url "/blog/admin" :text "Posts"}
                  {:url "/blog/admin/users" :text "Users"}
                  {:url "/blog/logout" :text "Logout"}])

(def includes {:jquery (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js")
               :default (include-css "/css/default.css")
               :reset (include-css "/css/reset.css")
               :blog.js (include-js "/js/blog.js")})

(def includes-show {:jquery (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js")
               :default (include-css "/css/modern.css")
               :reset (include-css "/css/modern-responsive.css")
               :carousel.js (include-js "/js/carousel.js")})
;; Helper partials
(defn static [file]
  (-> file resource slurp l/unescaped))
(defpartial build-head [incls]
            [:head
             [:title "The Noir Blog"]
             (map #(get includes %) incls)])
(defpartial build-head-x [incls]
            [:head
             [:title "The Picture Blog"]
             (map #(get includes-show %) incls)])

(defpartial link-item [{:keys [url cls text]}]
            [:li
             (link-to {:class cls} url text)])

;; Layouts

(defpartial main-layout [& content]
            (html5
              (build-head [:reset :default :jquery :blog.js])
              [:body
               [:div#wrapper
                [:div.content
                 [:div#header
                  [:h1 (link-to "/blog/" "The Noir blog")]
                  [:ul.nav
                   (map link-item main-links)]]
                 content]]]))

(defpartial admin-layout [& content]
            (html5
              (build-head [:reset :default :jquery :blog.js])
              [:body
               [:div#wrapper
                [:div.content
                 [:div#header
                  [:h1 (link-to "/blog/admin" "Admin")]
                  [:ul.nav
                   (map link-item admin-links)]]
                 content]]]))
(defpartial show-layout [& content]
            (html5
              (build-head-x [:reset :default :jquery :carousel.js])
         ( hicv/html->hiccup (slurp "src/milk/views/my.html") )))