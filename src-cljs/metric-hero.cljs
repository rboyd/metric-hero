(ns metric-hero)
;  (:require [clojure.browser.repl :as repl]))

(def camera (THREE/PerspectiveCamera. 75 (/ window/innerWidth window/innerHeight) 10 10000))
(def scene (THREE/Scene.))
(def projector (THREE/Projector.))
(def renderer (THREE/WebGLRenderer.))
(def objects (array))
(def object-to-file (atom {}))
(def clock (THREE/Clock.))
(def controls (THREE/FlyControls. camera))

(declare init-depth)
(declare init-post-processing)

(defn ^:export init []
  (.set (.-position camera) 400 650 1000)
  (doto controls
    (aset "movementSpeed" 1000)
    (aset "domElement" (.-domElement renderer))
    (aset "autoForward" false)
    (aset "rollSpeed" (/ Math/PI 24))
    (aset "dragToLook" true))
  (.lookAt camera (THREE/Vector3. 400 0 300))
  (.setSize renderer window/innerWidth window/innerHeight)
  (.appendChild document/body (.-domElement renderer))

  (init-depth)
  (init-post-processing))

(defn init-depth []
  (let [depth-shader (aget THREE/ShaderLib "depthRGBA")
        depth-uniforms (.clone THREE/UniformsUtils (.-uniforms depth-shader))
        shader-init (js-obj "fragmentShader" (.-fragmentShader depth-shader)
                            "vertexShader" (.-vertexShader depth-shader)
                            "uniforms" depth-uniforms)]
    (def depth-material (THREE/ShaderMaterial. shader-init))
    (set! (.-blending depth-material) THREE/NoBlending)))

(defn init-post-processing []
  (def composer (THREE/EffectComposer. renderer))
  (.addPass composer (THREE/RenderPass. scene camera))
  (def depth-target (THREE/WebGLRenderTarget. window/innerWidth window/innerHeight
                                               (js-obj "minFilter" THREE/NearestFilter
                                                       "magFilter" THREE/NearestFilter
                                                       "format" THREE/RGBAFormat)))
  (let [effect (THREE/ShaderPass. THREE/SSAOShader)]
    (set! (.-value (aget (.-uniforms effect) "tDepth")) depth-target)
    (.set (.-value (aget (.-uniforms effect) "size")) window/innerWidth window/innerHeight)
    
    (set! (.-value (aget (.-uniforms effect) "cameraNear")) (.-near camera))
    (set! (.-value (aget (.-uniforms effect) "cameraFar")) (.-far camera))
    (set! (.-renderToScreen effect) true)
    (.addPass composer effect)))

(declare scale-fn)
(declare render-nodes)

(defn ^:export render-node [node]
  (if (pos? (.-modified node))
    (let [width     (.-dx node)
          length    (.-dy node)
          height    (if (.hasOwnProperty node "children") 0 (scale-fn (.-modified node)))
          x         (+ (.-x node) (/ width 2))
          y         (/ height 2)
          z         (+ (.-y node) (/ length 2))
          node-geo  (THREE/CubeGeometry. width height length)
          m-params  (js-obj "color" 0xdddddd)
                           ; "opacity" (* .1 (.-depth node))
                           ; "transparent" true)
          material  (THREE/MeshBasicMaterial. m-params)
          node-mesh (THREE/Mesh. node-geo material)]
      (aset (.-position node-mesh) "x" x)
      (aset (.-position node-mesh) "y" y)
      (aset (.-position node-mesh) "z" z)
      (.add scene node-mesh)
      (.push objects node-mesh)
      (swap! object-to-file assoc node-mesh (str (.-name node) " " (.-modified node))))
    (render-nodes (.-children node))))

(defn ^:export render-nodes [nodes]
  (doall (map render-node nodes)))

(defn get-size [node]
  (.-size node))

(defn find-scale [root]
  (let [tseq (tree-seq (complement nil?) #(.-children %) root)
        dir? #(-> (.-children %) count pos?)
        files-only (remove dir? tseq)
        modified (filter pos? (map #(.-modified %) files-only))
        max-time (apply max modified)
        min-time (apply min modified)
        factor   (/ 400 (- max-time min-time))
        scale-fn #(* (- % min-time) factor)]
    scale-fn))

(declare nodes)
(defn ^:export parse-nodes [_ root]
  (def nodes root)
  (let [d3-treemap (-> (.treemap d3/layout)
                       (.size (array 800 600))
                       (.sticky true)
                       (.value get-size))]
    (def scale-fn (find-scale nodes))
    (.nodes d3-treemap nodes)
    (render-nodes (.-children nodes))))

(defn ^:export print-node [node]
  (.log js/console node))

(defn ^:export print-nodes [nodes]
  (doall (map print-node nodes)))

(defn click-handler [event]
  (.preventDefault event)
  (let [mouseX (dec (* 2 (/ (.-clientX event) (.-innerWidth js/window))))
        mouseY (inc (- (* 2 (/ (.-clientY event) (.-innerHeight js/window)))))
        vector  (THREE/Vector3. mouseX mouseY 0.5)
        cam-pos (.-position camera)]
    (.unprojectVector projector vector camera)
    (let [raycaster  (THREE/Raycaster. cam-pos (-> (.sub vector cam-pos)
                                                    .normalize))
          intersects (.intersectObjects raycaster objects)]
      (if (pos? (.-length intersects))
        (let [rand-color  (* (Math/random) 0xffffff)
              clicked-obj (.-object (aget intersects 0))]
          (.log js/console (@object-to-file clicked-obj))
          (-> clicked-obj
              .-material
              .-color
              (.setHex rand-color))
          (.render renderer scene camera))))))

(defn resize-handler [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (aset camera "aspect" (/ w h))
    (.updateProjectionMatrix camera)
    (.setSize renderer w h)))

(defn add-event-handlers []
  (.addEventListener js/document "mousedown" click-handler false)
  (.addEventListener js/window "resize" resize-handler false))

(defn animate []
  (js/requestAnimationFrame animate)
  (.update controls (.getDelta clock))
  (set! (.-overrideMaterial scene) depth-material)
  (.render renderer scene camera depth-target) 
  (set! (.-overrideMaterial scene) nil)
  (.render composer))

(defn ^:export render []
;  (repl/connect "http://localhost:9000/repl")
  (init)
  (.json js/d3 "output.json" parse-nodes)
  (add-event-handlers)
  (animate)) 