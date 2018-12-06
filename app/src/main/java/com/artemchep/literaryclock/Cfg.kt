package com.artemchep.literaryclock

import android.graphics.Color
import com.artemchep.config.common.SharedPrefConfig

/**
 * @author
 */
object Cfg : SharedPrefConfig("config") {

    const val KEY_WIDGET_UPDATE_SERVICE_ENABLED = "widget_update_service_enabled"
    const val KEY_WIDGET_TEXT_COLOR = "widget_text_color"

    /**
     * `true` if the user wants to launch a [foreground service][WidgetUpdateService]
     * to update the widget every minute, `false` otherwise.
     */
    var isWidgetUpdateServiceEnabled by configDelegate(KEY_WIDGET_UPDATE_SERVICE_ENABLED, false)

    var widgetTextColor by configDelegate(KEY_WIDGET_TEXT_COLOR, Color.WHITE)

}