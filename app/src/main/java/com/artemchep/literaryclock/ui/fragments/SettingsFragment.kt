package com.artemchep.literaryclock.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.artemchep.config.Config
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.R
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * @author Artem Chepurnoy
 */
class SettingsFragment : Fragment(), View.OnClickListener, Config.OnConfigChangedListener<String> {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navUpBtn.setOnClickListener(this)
        setupWidgetUpdaterPreference()
        setupWidgetColorPreference()
    }

    private fun setupWidgetUpdaterPreference() {
        var isBroadcasting = false

        altWidgetUpdaterTextView.setOnClickListener(this)
        altWidgetUpdaterSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isBroadcasting) {
                return@setOnCheckedChangeListener
            }

            isBroadcasting = true

            Cfg.edit(context!!) {
                Cfg.isWidgetUpdateServiceEnabled = isChecked
            }

            isBroadcasting = false
        }
    }

    private fun setupWidgetColorPreference() {
        var isBroadcasting = false

        val colors = arrayOf(Color.WHITE, Color.BLACK).toIntArray()
        palette.setFixedColumnCount(colors.size)
        palette.setColors(colors)
        palette.setOnColorSelectedListener { color ->
            if (isBroadcasting) {
                return@setOnColorSelectedListener
            }

            isBroadcasting = true

            Cfg.edit(context!!) {
                Cfg.widgetTextColor = color
            }

            isBroadcasting = false
        }
    }

    override fun onStart() {
        super.onStart()
        Cfg.observe(this)
        updateWidgetUpdateServiceEnabledPref()
        updateWidgetColorPref()
    }

    override fun onStop() {
        Cfg.removeObserver(this)
        super.onStop()
    }

    override fun onConfigChanged(keys: Set<String>) {
        if (Cfg.KEY_WIDGET_UPDATE_SERVICE_ENABLED in keys) {
            updateWidgetUpdateServiceEnabledPref()
        } else if (Cfg.KEY_WIDGET_TEXT_COLOR in keys) {
            updateWidgetColorPref()
        }
    }

    private fun updateWidgetUpdateServiceEnabledPref() {
        altWidgetUpdaterSwitch.isChecked = Cfg.isWidgetUpdateServiceEnabled
    }

    private fun updateWidgetColorPref() {
        try {
            palette.setSelectedColor(Cfg.widgetTextColor)
        } catch (_: Exception) {
            palette.setSelectedColor(Color.WHITE)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.altWidgetUpdaterTextView -> altWidgetUpdaterSwitch.toggle()
            R.id.navUpBtn -> findNavController().navigateUp()
        }
    }

}