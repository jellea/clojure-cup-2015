(ns clojure-cup-2015.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.js :as cljs]
            [cljs.tools.reader :as r]
            [cljsjs.codemirror]))

(enable-console-print!)

(defonce !state (atom {:result ""}))

(defn error! [error]
  (swap! !state assoc :error error))

(defn dismiss! [] (error! nil))

(defn q [selector] (.querySelector js/document selector))

(defn eval [in-str]
  (let [st (cljs/empty-state)]
    (cljs/eval-str st in-str 'fiddle.runtime
                   {:eval cljs/js-eval :source-map true :ns 'fiddle.runtime}
                   (fn [{:keys [error value]}]
                     (if error
                       (error! (->> error .-cause .-message))
                       (swap! !state assoc :text (str value)))))))

(memoize
  (let [cm (js/CodeMirror (.getElementById js/document "editor")
             #js {:value "(+ 1 4)"})]
    (.on cm "change" #(eval (-> cm .-doc .getValue)))))


(defn bang-bang []
  (let [{:keys [error] :as state} @!state]
    [:div
     (when error
       [:div.error
        [:a {:href "#"} [:i {:class "fa fa-check fa-lg" :on-click dismiss!}]]
        [:p "ERROR"]
        [:p error]])
     [:h2 "Bang bang"]
     [:p "=> " (:text @!state)]]))

(reagent/render-component [bang-bang]
                          (. js/document (getElementById "app")))


(defn on-js-reload [])
  ;; optionally touch your !state to force rerendering depending on
  ;; your application
  ;; (swap! !state update-in [:__figwheel_counter] inc)
  
