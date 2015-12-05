(ns clojure-cup-2015.editor
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure-cup-2015.common :refer [config !state]]))

(def opts {:matchBrackets true
           :lineNumbers false
           :autoCloseBrackets true
           :theme "monokai"
           :mode "clojure"})

(defn cm-editor
  [props cm-opts]
  (reagent/create-class
   {:component-did-mount
    (fn [this]
      (let [editor (.fromTextArea js/CodeMirror (reagent/dom-node this) (clj->js (merge opts cm-opts)))]
        (.on editor "change" #((:on-change props) (.getValue %)))
        (reagent/set-state this {:editor editor})))

    :should-component-update
    (fn [this]
      (let [editor  (:editor (reagent/state this))
            val     (:code (:code @!state))
            update? (not= val (.getValue editor))]
        (when update? (.setValue editor val))
        update?))

    :reagent-render
    (fn [_] [:textarea {:default-value (:default-value props)}])}))
