(ns clojure-cup-2015.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.js :as cljs]
            [cljs.tools.reader :as r]
            [cljsjs.codemirror]))

(defn q [selector] (.querySelector js/document selector))

(defn eval [in-str]
  (let [st (cljs/empty-state)]
    (cljs/eval-str st in-str 'fiddle.runtime
                   {:eval cljs/js-eval :source-map true :ns 'fiddle.runtime}
                   (fn [{:keys [error value]}]
                     (if error
                       (.error js/console error)
                       (println "=>" (pr-str value)))))))

(defn eval-input [e]
  (eval (->> "#cljs-text" q .-value)))

(defn start []
  )

(enable-console-print!)

(start)

(defonce app-state (atom {}))
(defn editor [])

(memoize (let [cm (js/CodeMirror (.getElementById js/document "editor"))]
           #js {:value "(+ 1 1)"}))

(defn bang-bang []
  [:div
   [:h2 "Bang bang"]
   [:form#bang {:on-click eval-input}
    [:textarea {:id "cljs-text", :rows 4, :cols 50}
     "(+ 3 22)"]
    [:input {:type :button, :value "Eval"}]]])

(reagent/render-component [bang-bang]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
