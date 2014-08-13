(ns fb-gallery-download.core
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [environ.core :refer [env]])
  (:gen-class))

(def access-token (env :facebook-token))

(defn authorize-url [url] (str url "?access_token=" access-token))

(defn pictures-base-url [id] (authorize-url (str "https://graph.facebook.com/v2.0/" id "/photos")))
(def albums-base-url (authorize-url "https://graph.facebook.com/v2.0/me/albums"))

(defn contents-of 
  "Return a map of json retrieved from the specified url"
  [url] (parse-string (:body (client/get url)) true))


(defn get-picture-info 
  "Retrieve a link for each image from a page of photos"
  [url] (let [contents (contents-of url)
              urls     (map #(:source (first (:images %))) (:data contents))
              next-link (:next (:paging contents))]
                {:data  urls, :next-link next-link}))

(defn get-album-info
  "Retrieve the id and the name of each album from a page of albums. 
  If no page of albums is specified return the first page" 
  ([]    (get-album-info albums-base-url))
  ([url] (let [contents  (contents-of url)
               albums    (map #(select-keys % [:id :name]) (:data contents))
               next-link (:next (:paging contents))]
                 {:data  albums, :next-link next-link})))

(defn traverse
  "Call retrieve-from on the provided url and then visit each data item returned. 
  Continue to retrieve data from the next-link until no next-link is returned" 
  [retrieve-from url visit]
    (let [{:keys [data next-link]} (retrieve-from url)]
      (doseq [item data] 
        (visit item))
      (if next-link
        (traverse retrieve-from next-link visit))))

(defn -main
  [& args]
      (traverse get-album-info albums-base-url #(println %)))
