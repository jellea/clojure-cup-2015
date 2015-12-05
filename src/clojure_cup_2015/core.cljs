(ns clojure-cup-2015.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.js :as cljs]
            [cljs.tools.reader :as r]
            [cljsjs.codemirror]))

(defonce app-state (atom {:text ""}))

(defn q [selector] (.querySelector js/document selector))

(defn eval [in-str]
  (let [st (cljs/empty-state)]
    (cljs/eval-str st in-str 'fiddle.runtime
                   {:eval cljs/js-eval :source-map true :ns 'fiddle.runtime}
                   (fn [{:keys [error value]}]
                     (if error
                       (.error js/console error)
                       (swap! app-state assoc :text (str value)))))))

(enable-console-print!)

(memoize
  (let [cm (js/CodeMirror (.getElementById js/document "editor")
             #js {:value "(+ 1 4)"})]
    (.on cm "change" #(eval (-> cm .-doc .getValue)))))

(defn bang-bang []
  [:div
   [:h2 "Bang bang"]
   [:p "=> " (:text @app-state)]])

(reagent/render-component [bang-bang]
                          (. js/document (getElementById "app")))


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
