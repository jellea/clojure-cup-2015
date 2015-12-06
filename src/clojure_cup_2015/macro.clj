(ns clojure-cup-2015.macro)

#_(def out (clojure.java.io/writer "log.txt" :append true))

(defn log [& args]
  (.write out (str (clojure.string/join " " (map str args)) \newline))
  (.flush out))

(defn read-resource [n]
  #_(log "Reading resource:" n)
  (some->> (str "code/" n ".cljs")
           clojure.java.io/resource
           clojure.java.io/file
           slurp))

(defmacro read-snippets [n]
  (->> (range 1 (inc n))
       (map (fn [n] [n (read-resource n)]))
       (into {})))
