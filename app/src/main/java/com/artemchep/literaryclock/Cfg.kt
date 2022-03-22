package com.artemchep.literaryclock

import com.artemchep.config.common.SharedPrefConfig

/**
 * @author
 */
object Cfg : SharedPrefConfig("config") {

    const val KEY_WIDGET_UPDATE_SERVICE_ENABLED = "widget_update_service_enabled"
    const val KEY_APP_THEME = "app_theme"
    const val APP_THEME_DARK = "dark"
    const val APP_THEME_LIGHT = "light"
    const val APP_THEME_AUTO = "auto"
    const val APP_THEME_DEFAULT = APP_THEME_AUTO

    /**
     * `true` if the user wants to launch a [foreground service][WidgetUpdateService]
     * to update the widget every minute, `false` otherwise.
     */
    var isWidgetUpdateServiceEnabled by configDelegate(KEY_WIDGET_UPDATE_SERVICE_ENABLED, true)

    var appTheme by configDelegate(KEY_APP_THEME, APP_THEME_DEFAULT)

}