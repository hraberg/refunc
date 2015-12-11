(ns todomvc.core
  (:require [clojure.string :as s]
            [refunc.core :refer (html)]))

;; Based on https://github.com/reagent-project/reagent/blob/master/examples/todomvc/src/todomvc/core.cljs

(enable-console-print!)

(def filters (array-map :all identity
                        :active (complement :completed)
                        :completed :completed))

(defonce app-state (atom {:todos (sorted-map)
                          :selected-filter (key (first filters))
                          :edited-todo nil
                          :counter 0}))

(defn add-todo [title]
  (let [{id :counter} (swap! app-state update :counter inc)]
    (swap! app-state assoc-in [:todos id] {:id id :title title :completed false})))

(defn toggle [id] (swap! app-state update-in [:todos id :completed] not))
(defn save [id title] (swap! app-state assoc-in [:todos id :title] title))
(defn delete [id] (swap! app-state update :todos dissoc id))
(defn start-edit [id] (swap! app-state assoc :edited-todo id))
(defn stop-edit [] (swap! app-state dissoc :edited-todo))
(defn select-filter [name] (swap! app-state assoc :selected-filter name))

(defn mmap [m f a] (->> m (f a) (into (empty m))))
(defn complete-all [v] (swap! app-state update :todos mmap map #(assoc-in % [1 :completed] v)))
(defn clear-completed [] (swap! app-state update :todos mmap remove #(get-in % [1 :completed])))

(def KEYS {:enter 13 :esc 27})

(defn todo-input [{:keys [on-save on-stop] :as props}]
  (let [stop #(do (if on-stop (on-stop))
                  (aset % "target" "value" ""))
        save #(let [v (aget % "target" "value")]
                (if-not (empty? v) (on-save v))
                (stop %))
        keymap {(:enter KEYS) save
                (:esc KEYS) stop}]
    (html
     [:input (merge (select-keys props [:className :defaultValue :placeholder])
                    {:autofocus true
                     :onBlur save
                     :onKeyDown #(some-> % .-which keymap (apply [%]))})])))

(defn todo-stats [{:keys [active completed selected-filter]}]
  (html
   [:div
    [:span.todo-count
     [:strong active] " " (case active 1 "item" "items") " left"]
    [:ul.filters
     (for [f (keys filters)]
       [:li [:a {:className (if (= f selected-filter) "selected")
                 :href (str "#/" (name f))}
             (s/capitalize (name f))]])]
    (when (pos? completed)
      [:button.clear-completed {:onClick clear-completed}
       "Clear completed " completed])]))

(defn todo-item [{:keys [id completed title editing]}]
  (html
   [:li {:className (str (if completed "completed ")
                         (if editing "editing"))
         :key id}
    [:div.view
     [:input.toggle {:type "checkbox" :checked completed
                     :onChange #(toggle id)}]
     [:label {:onDoubleClick #(start-edit id)} title]
     [:button.destroy {:onClick #(delete id)}]]
    (when editing
      ^{:did-mount #(.focus %)}
      [todo-input {:className "edit"
                   :defaultValue title
                   :on-save #(save id %)
                   :on-stop stop-edit}])]))

(defn todo-app [{:keys [todos edited-todo selected-filter]}]
  (let [items (vals todos)
        completed (->> items (filter :completed) count)
        active (- (count items) completed)]
    (html
     [:div
      [:section.todoapp
       [:header.header
        [:h1 "todos"]
        [todo-input {:className "new-todo"
                     :placeholder "What needs to be done?"
                     :on-save add-todo}]]
       (when (seq items)
         [:div
          [:section.main
           [:input.toggle-all {:type "checkbox" :checked (zero? active)
                               :onChange #(complete-all (pos? active))}]
           [:label {:htmlFor "toggle-all"} "Mark all as complete"]
           [:ul.todo-list
            (for [{:keys [id] :as todo} (filter (filters selected-filter) items)]
              [todo-item (assoc todo :editing (= id edited-todo))])]]
          [:footer.footer
           [todo-stats {:active active :completed completed :selected-filter selected-filter}]]])]
      [:footer.info
       [:p "Double-click to edit a todo"]
       [:p "Created by Håkan Råberg"]]])))

(defn on-hashchange []
  (let [hash (some-> js/location .-hash (subs 2) keyword)]
    (if (filters hash)
      (select-filter hash)
      (aset js/location "hash" (str "/" (name (:selected-filter @app-state)))))))

(.addEventListener js/window "hashchange" on-hashchange)
(on-hashchange)

(refunc.core/render! (.getElementById js/document "app") todo-app app-state)
