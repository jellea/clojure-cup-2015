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

(defmacro docstrings [ns vars]
  (let [result
        (into {} (map (fn [v]
                        [(str v) (:doc (meta (clojure.core/ns-resolve (find-ns ns) v)))])
                      vars))]
    result))
