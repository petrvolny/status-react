(ns status-im.ui.screens.extensions.views
  (:require-macros [status-im.utils.views :as views])
  (:require [re-frame.core :as re-frame]
            [status-im.i18n :as i18n]
            [status-im.ui.components.icons.vector-icons :as vector-icons]
            [status-im.ui.components.list.views :as list]
            [status-im.ui.components.react :as react]
            [status-im.ui.components.status-bar.view :as status-bar]
            [status-im.ui.components.toolbar.view :as toolbar]
            [status-im.ui.components.toolbar.actions :as toolbar.actions]
            [status-im.ui.screens.extensions.styles :as styles]))

(def mailserver-icon
  [react/view (styles/mailserver-icon true)
   [vector-icons/icon :icons/mailserver {:color :white}]])

(defn- render-extension [{:keys [name url active?]}]
  [list/deletable-list-item {:type      :extensions
                             :id        url
                             :on-delete #(do
                                           (re-frame/dispatch [:set-swipe-position :extensions url false])
                                           (re-frame/dispatch [:extensions.ui/uninstall-extension-pressed url]))}
   [list/list-item-with-checkbox
    {:checked?        active?
     :on-value-change #(re-frame/dispatch [:extensions.ui/activation-checkbox-pressed url %])}
    [list/item
     mailserver-icon
     [list/item-content
      [list/item-primary name]
      [list/item-secondary url]]]]])

(views/defview extensions-settings []
  (views/letsubs [extensions [:extensions/all-extensions]]
    [react/view {:flex 1}
     [status-bar/status-bar]
     [toolbar/toolbar {}
      toolbar/default-nav-back
      [toolbar/content-title (i18n/label :t/extensions)]
      [toolbar/actions
       [(toolbar.actions/add false #(re-frame/dispatch [:extensions.ui/add-extension-pressed]))]]]
     [react/view styles/wrapper
      [list/flat-list {:data                    (vals extensions)
                       :default-separator?      false
                       :key-fn                  :id
                       :render-fn               render-extension
                       :content-container-style (merge (when (zero? (count extensions))
                                                         {:flex-grow 1})
                                                       {:justify-content :center})
                       :empty-component         [react/text {:style styles/empty-list}
                                                 (i18n/label :t/no-extension)]}]]]))

(defn- render-selection-item [label-key on-select]
  (fn [item]
    [react/touchable-highlight {:on-press #(on-select item)}
     [react/view {:style {:height 64 :margin-horizontal 16}}
      [list/item-content
       [list/item-primary (get item label-key)]]]]))

(views/defview selection-modal-screen []
  (views/letsubs [{:keys [items label title on-select]} [:get-screen-params :selection-modal-screen]]
    [react/view {:flex 1}
     [status-bar/status-bar]
     [toolbar/toolbar {}
      toolbar/default-nav-close
      [toolbar/content-title title]]
     [react/view styles/wrapper
      [list/flat-list {:data                    items
                       :default-separator?      false
                       :key-fn                  label
                       :render-fn               (render-selection-item label on-select)
                       :content-container-style {:justify-content :center}
                       :empty-component         [react/text {:style styles/empty-list}
                                                 "No items"]}]]]))