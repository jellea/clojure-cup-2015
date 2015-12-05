(ns clojure-cup-2015.quil-symbols
  (:require [clojure.string :as s]
            [clojure.string :as str]))

(def functions
  '[;; COLOR ;;
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
    ;; TYPOGRAPHY ;;
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
    ;; IMAGE ;;
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
    ;; TRANSFORM ;;
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
    ;; RENDERING ;;
    hint
    ;; with-graphics
    ;; Shaders
    load-shader
    ;; MATH ;;
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
    ;; DATA ;;
    ;; Conversion
    binary
    hex
    unbinary
    unhex
    ;; STATE ;;
    set-state!
    state
    state-atom
    ;; SHAPE ;;
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
    ;; LIGHTS, CAMERA ;;
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
    ;; ENVIRONMENT ;;
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
    ;;sketch
    target-frame-rate
    width
    ;; INPUT ;;
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
    ;; OUTPUT ;;
    ;; Files
    end-raw
    ;; Image
    save
    save-frame
    ;; STRUCTURE ;;
    delay-frame
    exit
    no-loop
    pop-style
    push-style
    redraw
    start-loop
    ;; MIDDLEWARE ;;
    ;; fun-mode
    ;; navigation-2d
    ;; navigation-3d
    ])

(def macros '[defsketch with-fill with-stroke with-translation])

(def live-sketches (atom {}))

(defn sketch-wrapper [& {:keys [host] :as opts}]
  (when-let [sketch (get @live-sketches host)]
    (.exit sketch))
  (let [new-sketch (apply quil.core/sketch (apply concat opts))]
    (swap! live-sketches assoc host new-sketch))
)

(defn import-symbols-src
  "A hack to make quil functions available in the main namespace, generates a
  string that looks like (def fill quil.core/fill), which we then eval."
  []
  (str
   (str/join "\n" (map #(str "(def " % " quil.core/" % ")") functions))
   "(def sketch clojure-cup-2015.quil-symbols/sketch-wrapper)"))

;; (defn make-require-str []
;;   (str
;;    "(:require [quil.core :as q \n  :refer [\n"
;;    (s/join " " functions)
;;    "\n]\n"
;;    "  :refer-macros [\n"
;;    (s/join " " macros)
;;    "\n]]\n"
;;    "[quil.middleware :as m])")
;;   )
