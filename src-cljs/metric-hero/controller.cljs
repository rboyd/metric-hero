(ns metric-hero.controller)

(defn accelerator []
  (-> (Gamepad/getStates) first .-rightShoulder1))
