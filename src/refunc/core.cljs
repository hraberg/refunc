(ns refunc.core
  (:require [clojure.string :as s]
            [cljsjs.react.dom]))

;; From https://github.com/weavejester/hiccup/blob/master/src/hiccup/compiler.clj
(def ^{:doc "Regular expression that parses a CSS-style id and class from an element name."
       :private true}
  re-tag #"([^\s\.#]+)(?:#([^\s\.#]+))?(?:\.([^\s#]+))?")

(defn html
  "Takes Hiccup style Clojure data and returns a React component. Uses
  React names in camel case for props and doesn't do any conversion."
  [hiccup]
  (if (vector? hiccup)
    (let [[tag & [attributes & children :as all-children]] hiccup
          component? (fn? tag)
          [_ tag id class] (if component?
                             [nil tag]
                             (re-find re-tag (name tag)))
          [attributes children] (if ((some-fn map? object?) attributes)
                                  [attributes children]
                                  [nil all-children])
          attributes (cond-> attributes
                       id (assoc :id id)
                       class (update :className #(s/join " " (cond-> (s/split class #"\.")
                                                               % (conj %)))))
          children (->> children
                        (mapcat #(cond-> %
                                   (not (seq? %)) vector))
                        (map html))
          props (reduce-kv (fn [js k v]
                             (doto js (aset (name k) v))) #js {} attributes)]
      (apply (.-createElement js/React) tag props children))
    hiccup))

;;; For destructuring of JavaScript objects and props.
(extend-type object
  ILookup
  (-lookup
    ([this k]
     (-lookup this k nil))
    ([this k not-found]
     (if (.hasOwnProperty this (name k))
       (aget this (name k))
       not-found))))

(defn render-once!
  "Mount the stateless functional component f at node. Optional
  did-update is called on frame completion."
  [node f & [state did-update]]
  (.render js/ReactDOM
           (cond-> f
             (fn? f) (apply [(cond-> state
                               (satisfies? IDeref state) deref)]))
           node
           did-update))

(defn render!
  "Mount the stateless functional component f at node. Will re-render
  using requestAnimationFrame on state changes if
  IWatchable. See render-once!"
  [node f & [state did-update]]
  (let [tick-requested? (atom false)
        tick (fn []
               (when (compare-and-set! tick-requested? false true)
                 (js/requestAnimationFrame
                  #(do (reset! tick-requested? false)
                       (render-once! node f state did-update)))))]
    (when (satisfies? IWatchable state)
      (add-watch state :render tick))
    (tick)))
