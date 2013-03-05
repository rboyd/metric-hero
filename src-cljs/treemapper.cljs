(ns treemapper)

(def camera (THREE/PerspectiveCamera. 75 (/ window/innerWidth window/innerHeight) 1 10000))
(def scene (THREE/Scene.))
(def projector (THREE/Projector.))
(def r (THREE/CanvasRenderer.))
(def objects (array))
(def object-to-file (atom {}))

(defn ^:export init []
  (.set (.-position camera) 400 650 1000)
  (.lookAt camera (THREE/Vector3. 400 0 300))
  (.setSize r window/innerWidth window/innerHeight)
  (.appendChild document/body (.-domElement r))
  (.render r scene camera))

(declare scale-fn)
(declare render-nodes)

(defn ^:export render-node [node]
  (let [width     (.-dx node)
        length    (.-dy node)
        height    (scale-fn (.-modified node))
        x         (+ (.-x node) (/ width 2))
        y         (/ height 2)
        z         (+ (.-y node) (/ length 2))
        node-geo  (THREE/CubeGeometry. width height length)
        m-params  (js-obj "color" 0xff0000
                          "opacity" (* .1 (.-depth node))
                          "transparent" true)
        material  (THREE/MeshBasicMaterial. m-params)
        node-mesh (THREE/Mesh. node-geo material)]
    (aset (.-position node-mesh) "x" x)
    (aset (.-position node-mesh) "y" y)
    (aset (.-position node-mesh) "z" z)
    (.add scene node-mesh)
    (.push objects node-mesh)
    (swap! object-to-file assoc node-mesh (.-name node))
    (.render r scene camera))
  (render-nodes (.-children node)))

(defn ^:export render-nodes [nodes]
  (doall (map render-node nodes)))

(defn get-size [node]
  (.-size node))

(defn find-scale [nodes]
  (let [max-time (reduce max (map #(.-modified %) nodes))
        min-time (reduce min (map #(.-modified %) nodes))
        factor   (/ 100 (- max-time min-time))
        scale-fn #(* (- % min-time) factor)]
    scale-fn))

(declare nodes)
(defn ^:export parse-nodes [_ root]
  (def nodes root)
  (let [d3-treemap (-> (.treemap d3/layout)
                       (.size (array 800 600))
                       (.sticky true)
                       (.value get-size))]
    (def scale-fn (find-scale (.-children root)))
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
          (.render r scene camera))))))

(defn resize-handler [event]
  (let [w (.-innerWidth js/window)
        h (.-innerHeight js/window)]
    (aset camera "aspect" (/ w h))
    (.updateProjectionMatrix camera)
    (.setSize r w h)))

(defn add-event-handlers []
  (.addEventListener js/document "mousedown" click-handler false)
  (.addEventListener js/document "resize" resize-handler false))

(defn ^:export render []
  (init)
  (.json js/d3 "output.json" parse-nodes)
  (add-event-handlers))
