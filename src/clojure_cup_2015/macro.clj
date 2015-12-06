(ns clojure-cup-2015.macro)

(defn read-resource [n]
  (some->> (str "code/" n ".cljs")
           clojure.java.io/resource
           clojure.java.io/file
           slurp))

(defmacro read-snippets [n]
  (->> (range 1 (inc n))
       (map (fn [n] [n (read-resource n)]))
       (into {})))
