(ns fb-gallery-download.extra
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [clojure.java.io :as io]
            [environ.core :refer [env]])
  (:gen-class))

(defn copy 
  "Copy from an input stream to an output stream"
  [uri file]
  (with-open [in (io/input-stream uri), out (io/output-stream file)]
    (io/copy in out)))

(defn download-pictures []
  (traverse
       get-picture-info
       pictures-base-url
       #(do (println %)
            (copy % (io/file "/tmp/test" (str (System/nanoTime) ".jpg" ))))))                              

