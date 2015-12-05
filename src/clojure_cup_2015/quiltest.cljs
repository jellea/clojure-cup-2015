(ns clojure-cup-2015.quiltest
  (:require [quil.core :as q :refer-macros [with-translation defsketch]]
            [quil.middleware :as m]

            ))

(enable-console-print!)

(println "Edits to this text should show up in your developer foobaz")


; define function which draws spiral
(defn draw []
  ; make background white
  (q/background 255)

  ; move origin point to centre of the sketch
  ; by default origin is in the left top corner
  (with-translation [(/ (q/width) 2) (/ (q/height) 2)]
   ; parameter t goes 0, 0.01, 0.02, ..., 99.99, 100
   (doseq [t (range 0 100 0.01)]
     ; draw a point with x = t * sin(t) and y = t * cos(t)
     (q/point (* t (q/sin t))
              (* t (q/cos t))))))

;; ; run sketch
;; (defsketch trigonometry
;;   :host "canvas-id"
;;   :size [300 300]
;;   :draw draw)

; run sketch
(defsketch shapes-and-colors
  :host "canvas-id"
  :size [300 300]
  :draw (fn []))
