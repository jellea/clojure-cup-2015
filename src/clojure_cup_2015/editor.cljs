(ns clojure-cup-2015.editor
  (:require [reagent.core :as reagent]
            [clojure-cup-2015.common :refer [config !state !tooltip]]
            [clojure-cup-2015.quil-symbols :as quil-symbols :refer [quildocs]]
            [cljs.js :as cljs]
            [cljs.tools.reader]
            [dommy.core :as d]))

(defn debounce
  ([f] (debounce f 1000))
  ([f timeout]
   (let [id (atom nil)]
     (fn [evt]
       (js/clearTimeout @id)
       (reset! id (js/setTimeout f timeout))))))

(def opts {:matchBrackets true
           :lineNumbers false
           :autoCloseBrackets true
           :theme "monokai"
           :mode "clojure"
           :lineWrapping true})

(defn move-canvas
  [cm canvas-id]
  (when canvas-id
    (let [canvas (.getElementById js/document (str canvas-id "_holder"))
          cmheight (.heightAtLine cm (+ (.-line (.getCursor cm)) 1) "local")
          height (min (max (- cmheight 300) 15))]
      (set! (.. canvas -style -transform) (str "translate(-30px," height "px)")))))

(defn outer-sexp
  "Returns the outer sexp"
  [cm]
  (if-not (-> cm (.getTokenAt (.getCursor cm))
              .-state
              .-indentStack)
    (prn "not in from")

    (let [cur-cursor (.getCursor cm)]
      (while (-> cm (.getTokenAt (.getCursor cm)) .-state .-indentStack)
        (.moveH cm -1 "char"))
      (let [start (.getCursor cm)]
        (.moveH cm 1 "char")
        (while (-> cm (.getTokenAt (.getCursor cm)) .-state .-indentStack)
          (.moveH cm 1 "char"))
        (let [end (.getCursor cm)]
          (.setSelection cm start end)
          (let [selection (.getSelection cm)]
            (.setCursor cm cur-cursor)
            selection))))))


(defn add-inline
  "Add a inline comment/result/documentation what ever"
  [{:keys [line ch text]} editor]
  (let [dom-node (.createElement js/document "span")]
    (set! (.-innerHTML dom-node) text)
    (.setBookmark (.-doc editor) #js {:line line :ch ch} #js {:widget dom-node})))

(defn error! [error]
  (swap! !state assoc :error error))

(defn dismiss! [] (error! nil))

(defonce cljs-compiler-state (cljs/empty-state))

(defn warning-hook [& args]
  (throw (ex-info "Cljs warning" {:args args})))


(defn find-error [id {:keys [error value]}]
  (if error
    (do
      (error! {:message (->> error .-cause .-message)
               :id id})
      (swap! !state assoc :result nil))
    (do
      (dismiss!)
      (swap! !state assoc :result (str value)))))

(defn eval
  ([name-space in-str]
   (eval name-space in-str #()))

  ([name-space in-str callback]
   (let [st cljs-compiler-state]
     (prn name-space)
     (binding [cljs.analyzer/*cljs-warning-handlers* [warning-hook]]
       (cljs/eval-str st in-str (symbol name-space)
                      {:eval cljs/js-eval
                       :ns (symbol name-space)
                       ;;:verbose true

                       ;; don't ask me why this works. It stops Clojurescript from complaining that
                       ;; *load-fn* isn't defined
                       :load (fn [_ cb] (cb {:lang :clj :source ""}))}
                      callback)))))

(defn ns-str [ns]
  (str "(ns " ns " (:require [quil.core :as q] [quil.middleware :as m]))"))


(defn handle-mouse-over [ns editor event]
  (let [left (.-pageX event)
        top (.-pageY event)
        line-char (.coordsChar editor #js {"left" left
                                          "top" top })
        char (.-ch line-char)
        line (.-line line-char)]

    (if-let [doc (get quildocs (.-string
                                (.getTokenAt editor #js {"ch" char "line" line})))]
      (reset! !tooltip {:left left :top top :doc doc}))))

(defn cm-editor
  "CodeMirror reagent component"
  [props cm-opts]
  (reagent/create-class
   {:component-did-mount
    (fn [this]
      (let [id (:id props)
            name-space (str (:id props) ".user")
            dom-node (reagent/dom-node this)
            opts (clj->js (merge opts cm-opts))
            editor (.fromTextArea js/CodeMirror dom-node opts)]
        (eval name-space
              (str (ns-str name-space)
                   (quil-symbols/import-symbols-src)
                   (.getValue editor))
              (partial find-error id))
                                        ; (add-inline {:line 0 :ch 100 :text "hi"} editor)
        (when (:monoline props)
          (js/oneLineCM editor))
        (.on editor "change" (debounce #(eval name-space
                                              (.getValue editor)
                                              (partial find-error id))))

        (d/listen! (.getWrapperElement editor) :mouseover #(handle-mouse-over name-space editor %))

        (.on editor "cursorActivity" #(move-canvas % (:id props)))
        (reagent/set-state this {:editor editor})))

    ; :did-unmount
    ; (fn [this]
    ;  (let [sketches quil-symbols/live-sketches
    ;        dom-node (reagent/dom-node this)
    ;        editor (.fromTextArea js/CodeMirror dom-node opts)]
    ;   (doseq [e ["change" "cursorActivity"]]
    ;     (.off editor e))
    ;   (.exit (get sketches (:id props)))
    ;   (swap! sketches dissoc (:id props))))

    :should-component-update
    (fn [this]
      (let [editor  (:editor (reagent/state this))
            value   (:code (:code @!state))
            update? (not= value (.getValue editor))]
        (when update? (.setValue editor value))
        update?))

    :reagent-render
    (fn [_] [:textarea {:default-value (:default-value props)}])}))
