package com.artemchep.literaryclock.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import com.artemchep.config.Config
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.databinding.FragmentSettingsBinding
import com.artemchep.literaryclock.utils.ext.setOnApplyWindowInsetsListener
import com.artemchep.literaryclock.utils.wrapInStatusBarView

/**
 * @author Artem Chepurnoy
 */
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(),
    View.OnClickListener,
    Config.OnConfigChangedListener<String> {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSettingsBinding
        get() = FragmentSettingsBinding::inflate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = super.onCreateView(inflater, container, savedInstanceState)
        .let {
            wrapInStatusBarView(it)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnApplyWindowInsetsListener { insets ->
            viewBinding.scrollView.updatePadding(
                bottom = insets.systemWindowInsetBottom,
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )
            viewBinding.navUpBtnContainer.updatePadding(
                right = insets.systemWindowInsetRight,
                left = insets.systemWindowInsetLeft
            )

            view.findViewById<View>(R.id.statusBarBg).apply {
                layoutParams.height = insets.systemWindowInsetTop
                requestLayout()
            }

            insets.consumeSystemWindowInsets()
        }

        viewBinding.themeSpinner.apply {
            val themes = arrayOf(
                getString(R.string.settings_theme_auto) to Cfg.APP_THEME_AUTO,
                getString(R.string.settings_theme_dark) to Cfg.APP_THEME_DARK,
                getString(R.string.settings_theme_light) to Cfg.APP_THEME_LIGHT
            )

            // Create adapter
            val adapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                themes.map { it.first }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Setup spinner
            val selection = themes
                .indexOfFirst { it.second == Cfg.appTheme }
                .takeUnless { it == -1 }
            // resort to a default theme
                ?: themes.indexOfFirst { it.second == Cfg.APP_THEME_DEFAULT }
            setAdapter(adapter)
            setSelection(selection)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // Apply the selection
                    Cfg.edit(requireContext()) {
                        Cfg.appTheme = themes[position].second
                    }
                }
            }
        }

        viewBinding.navUpBtn.setOnClickListener(this)
        setupWidgetUpdaterPreference()
        setupWidgetColorPreference()
    }

    private fun setupWidgetUpdaterPreference() {
        var isBroadcasting = false

        viewBinding.altWidgetUpdaterTextView.setOnClickListener(this)
        viewBinding.altWidgetUpdaterSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isBroadcasting) {
                return@setOnCheckedChangeListener
            }

            isBroadcasting = true

            Cfg.edit(requireContext()) {
                Cfg.isWidgetUpdateServiceEnabled = isChecked
            }

            isBroadcasting = false
        }
    }

    private fun setupWidgetColorPreference() {
        var isBroadcasting = false

        val colors = arrayOf(Color.WHITE, Color.BLACK).toIntArray()
        viewBinding.palette.setFixedColumnCount(colors.size)
        viewBinding.palette.setColors(colors)
        viewBinding.palette.setOnColorSelectedListener { color ->
            if (isBroadcasting) {
                return@setOnColorSelectedListener
            }

            isBroadcasting = true

            Cfg.edit(requireContext()) {
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
        viewBinding.altWidgetUpdaterSwitch.isChecked = Cfg.isWidgetUpdateServiceEnabled
    }

    private fun updateWidgetColorPref() {
        try {
            viewBinding.palette.setSelectedColor(Cfg.widgetTextColor)
        } catch (_: Exception) {
            viewBinding.palette.setSelectedColor(Color.WHITE)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.altWidgetUpdaterTextView -> viewBinding.altWidgetUpdaterSwitch.toggle()
            R.id.navUpBtn -> findNavController().navigateUp()
        }
    }

}
