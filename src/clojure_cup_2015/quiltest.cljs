(ns clojure-cup-2015.quiltest
  (:require [quil.core :as q
             :refer [
;;;; COLOR ;;;;

;; Creating & Reading
alpha
blend-color
blue
brightness
color
color-mode
current-fill
current-stroke
green
hue
lerp-color
red
saturation

;; Setting
background
background-float
background-image
background-int
fill
fill-float
fill-int
no-fill
no-stroke
stroke
stroke-float
stroke-int

;; Utility Macros
;; with-fill
;; with-stroke

;;;; TYPOGRAPHY ;;;;

;; Attributes
text-align
text-leading
text-mode
text-size
text-width

;; Loading & Displaying
available-fonts
create-font
load-font
text
text-char
text-font
text-num

;; Metrics
text-ascent
text-descent

;;;; IMAGE ;;;;
create-image
resize

;; Loading & Displaying
image
image-mode
load-image
no-tint
request-image
tint
tint-float
tint-int

;; Pixels
blend
copy
display-filter
get-pixel
image-filter
pixels
set-image
set-pixel
update-pixels

;; Rendering
create-graphics

;;;; TRANSFORM ;;;;
apply-matrix
pop-matrix
print-matrix
push-matrix
reset-matrix
rotate
rotate-x
rotate-y
rotate-z
scale
shear-x
shear-y
translate

;; Utility Macros
;; with-rotation
;; with-translation

;;;; RENDERING ;;;;
hint
;; with-graphics

;; Shaders
load-shader

;;;; MATH ;;;;

;; Calculation
abs
ceil
constrain
dist
exp
floor
lerp
log
mag
map-range
norm
pow
round
sq
sqrt

;; Random
noise
noise-detail
noise-seed
random
random-gaussian
random-seed

;; Trigonometry
acos
asin
atan
atan2
cos
degrees
radians
sin
tan

;;;; DATA ;;;;

;; Conversion
binary
hex
unbinary
unhex

;;;; STATE ;;;;
set-state!
state
state-atom

;;;; SHAPE ;;;;

;; 2D Primitives
arc
ellipse
line
point
quad
rect
triangle

;; 3D Primitives
box
sphere
sphere-detail

;; Attributes
ellipse-mode
no-smooth
rect-mode
smooth
stroke-cap
stroke-join
stroke-weight

;; Curves
bezier
bezier-detail
bezier-point
bezier-tangent
curve
curve-detail
curve-point
curve-tangent
curve-tightness

;; Loading & Displaying
load-shape
shape
shape-mode

;; Vertex
begin-contour
begin-shape
bezier-vertex
curve-vertex
end-contour
end-shape
quadratic-vertex
texture
texture-mode
vertex

;;;; LIGHTS, CAMERA ;;;;

;; Camera
begin-camera
camera
end-camera
frustum
ortho
perspective
print-camera
print-projection

;; Coordinates
model-x
model-y
model-z
screen-x
screen-y
screen-z

;; Lights
ambient-light
directional-light
light-falloff
light-specular
lights
no-lights
normal
point-light
spot-light

;; Material Properties
ambient
ambient-float
ambient-int
emissive
emissive-float
emissive-int
shininess
specular

;;;; ENVIRONMENT ;;;;
current-frame-rate
current-graphics
cursor
cursor-image
;;defsketch
focused
frame-count
frame-rate
height
no-cursor
sketch
target-frame-rate
width

;;;; INPUT ;;;;

;; Keyboard
key-as-keyword
key-code
key-coded?
key-pressed?
raw-key

;; Mouse
mouse-button
mouse-pressed?
mouse-x
mouse-y
pmouse-x
pmouse-y

;; Time & Date
day
hour
millis
minute
month
seconds
year

;;;; OUTPUT ;;;;

;; Files
end-raw

;; Image
save
save-frame

;;;; STRUCTURE ;;;;
delay-frame
exit
no-loop
pop-style
push-style
redraw
start-loop

;;;; MIDDLEWARE ;;;;
;; fun-mode
;; navigation-2d
;; navigation-3d
]
             :refer-macros [with-translation defsketch]]
            [quil.middleware :as m]

            ))

(enable-console-print!)

(defn create-canvas [id]
  (let [el (.createElement js/document "canvas")]
    (aset el "id" (str id))
    (aset el "style" "clear: both;")
    (.appendChild (.-body js/document) el)))


; define function which draws spiral
(defn draw []
  ; make background white
  (background 255)

  ; move origin point to centre of the sketch
  ; by default origin is in the left top corner
  (with-translation [(/ (q/width) 2) (/ (q/height) 2)]
   ; parameter t goes 0, 0.01, 0.02, ..., 99.99, 100
   (doseq [t (range 0 100 0.01)]
     ; draw a point with x = t * sin(t) and y = t * cos(t)
     (point (* t (q/sin t))
              (* t (q/cos t))))))

(create-canvas "trigonometry")
(defsketch trigonometry
  :host "trigonometry"
  :size [300 300]
  :draw draw)



(create-canvas "shapes")
(defsketch shapes
  :host "shapes"
  :size [300 300]
  :draw (fn []
          ;;            x1  y1  x2 y2
          (q/rect       50  50  100 100)

          ;;             x   y  width  height
          (q/ellipse    90 250     90     70)

          ;;            x1  y1    x2 y2     x3  y3
          (q/triangle  200  20   175 75    250  75)

          ;;            x1   y1    x2  y2
          (q/line      230  150   290 220)
          (q/line      230  220   290 150)
          ))

(defn fill-orange []
  (q/fill 252 90 44))

(defn fill-pink []
  (q/fill 241 104 176))

(defn fill-blue []
  (q/fill 45 119 242))

(defn black-stroke []
  (q/stroke 0 0 0))

(defn draw-color-and-shape []
  (fill-orange)
  ;;            x1  y1  x2 y2
  (q/rect       50  50  100 100)

  (fill-blue)
  (q/no-stroke)
  ;;             x   y  width  height
  (q/ellipse    90 250     90     70)

  (fill-pink)
  (black-stroke)
  ;;            x1  y1    x2 y2     x3  y3
  (q/triangle  200  20   175 75    250  75)

  ;;            x1   y1    x2  y2
  (q/line      230  150   290 220)
  (q/line      230  220   290 150))

(create-canvas "color-and-shape")
(defsketch color-and-shape
  :host "color-and-shape"
  :size [300 300]
  :draw draw-color-and-shape)



(create-canvas "pink-triangles")
(defn draw-pink-triangles []
  (no-stroke)
  (fill 244 213 221) ; #f4d5dd
  (triangle 20 20, 60 90, 15 60)
  (fill 249 202 216) ; #f9cad8
  (triangle 220 210, 280 260, 215 240)
  (fill 232 181 188) ; #e8b5bc
  (triangle 150 40, 227 50, 90 170))

(defsketch pink-triangles
  :host "pink-triangles"
  :size [300 300]
  :draw draw-pink-triangles)
