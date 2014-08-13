fb-gallery-download
===================

I lost some photos and wanted to re-download them from facebook, and I wrote this project to allow me to slightly automate that process using the api. It also let me play with clojure a bit more. 

It uses environ for configuration so a facebook authentication token should be made available, for example in ~/.lein/profiles.clj  

```clojure
{:user {:env {:facebook-token "A Very long auth token"}}}
```

You can create authentication tokens easily from [here](https://developers.facebook.com/tools/explorer/).

To download a gallery you first need to find it's id. You can list all galleries and their id's by running the project: `lein run`: 

This will execute the following code:

```clojure
(traverse get-album-info albums-base-url #(println %))
```
which will print out gallery information in the following format:

```clojure
{:name South Korea, :id 10159121927391999}
{:name Mobile Uploads, :id 449293319999}
{:name Timeline Photos, :id 299993591996}
{:name Pumpkins, :id 99151791999991999}
{:name Girona, :id 101599913669999}
...
```

You can use this information to find the links of all the photos in a specific gallery:

```clojure
 (traverse get-picture-info (pictures-base-url 10159121927391999) #(println %))
```

Or even download them:

```clojure
(defn copy 
  "Copy from an input stream to an output stream"
  [uri file]
  (with-open [in (io/input-stream uri), out (io/output-stream file)]
    (io/copy in out)))

 (traverse
       get-picture-info
       (pictures-base-url 10159121927391999)
       #(do (println %)
            (copy % (io/file "/tmp/test" (str (System/nanoTime) ".jpg" )))))
```
Facebook makes the images available in multiple sizes - the code will return information about the first version available which is probably the largest - though the image resolutions are avaliable so we could find this out in a more safe way.

Obviously a small bit of extra work would allow me to automate it fully and I could also go on to create a friendlier CLI interface. 

