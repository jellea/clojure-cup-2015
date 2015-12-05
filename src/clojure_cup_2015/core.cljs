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

(defn try-it [e]
  (eval (->> "#my-text" q .-value)))

(enable-console-print!)

(defn editor [])

(memoize (let [cm (js/CodeMirror (.getElementById js/document "editor"))]
           #js {:value "(+ 1 1)"}))

(defn bang-bang []
  [:div
   [:h1 "WORDL HELLO"]
   [:form {:on-click try-it}
    [:input {:id "my-text", :type :text, :value "(+ 3 11)"}]
    [:input {:type :button, :value "Eval"}]]])

(reagent/render-component [bang-bang]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
