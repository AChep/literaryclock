package com.artemchep.literaryclock

import com.artemchep.config.common.SharedPrefConfig

/**
 * @author
 */
object Cfg : SharedPrefConfig("config") {
    private const val COLOR_OVERRIDE_UNSET = Long.MIN_VALUE

    const val KEY_WIDGET_UPDATE_SERVICE_ENABLED = "widget_update_service_enabled"
    const val KEY_APP_THEME = "app_theme"
    const val KEY_DREAM_TEXT_COLOR = "dream_text_color"
    const val KEY_DREAM_ACCENT_COLOR = "dream_accent_color"
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

    private var dreamTextColorOverrideRaw by configDelegate(KEY_DREAM_TEXT_COLOR, COLOR_OVERRIDE_UNSET)

    private var dreamAccentColorOverrideRaw by configDelegate(KEY_DREAM_ACCENT_COLOR, COLOR_OVERRIDE_UNSET)

    var dreamTextColor: Int?
        get() = dreamTextColorOverrideRaw
            .takeUnless { it == COLOR_OVERRIDE_UNSET }
            ?.toInt()
        set(value) {
            dreamTextColorOverrideRaw = value?.toLong() ?: COLOR_OVERRIDE_UNSET
        }

    var dreamAccentColor: Int?
        get() = dreamAccentColorOverrideRaw
            .takeUnless { it == COLOR_OVERRIDE_UNSET }
            ?.toInt()
        set(value) {
            dreamAccentColorOverrideRaw = value?.toLong() ?: COLOR_OVERRIDE_UNSET
        }

}
