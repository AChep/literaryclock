package com.artemchep.literaryclock.ui.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.artemchep.config.Config
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.databinding.FragmentDreamSettingsBinding
import com.artemchep.literaryclock.services.dream.DreamAppearance
import com.google.android.material.button.MaterialButton
import com.google.android.material.color.MaterialColors
import yuku.ambilwarna.AmbilWarnaDialog

class DreamSettingsFragment : BaseFragment<FragmentDreamSettingsBinding>(),
    Config.OnConfigChangedListener<String> {

    override val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDreamSettingsBinding
        get() = FragmentDreamSettingsBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.topAppBar.setNavigationOnClickListener {
            navigateUp()
        }
        viewBinding.textColorRow.setOnClickListener {
            showColorPicker(
                initialColor = DreamAppearance.resolveEffectiveTextColor(requireContext()),
                onColorPicked = ::setDreamTextColor,
            )
        }
        viewBinding.textColorResetButton.setOnClickListener {
            setDreamTextColor(null)
        }
        viewBinding.accentColorRow.setOnClickListener {
            showColorPicker(
                initialColor = DreamAppearance.resolveEffectiveAccentColor(),
                onColorPicked = ::setDreamAccentColor,
            )
        }
        viewBinding.accentColorResetButton.setOnClickListener {
            setDreamAccentColor(null)
        }
        updateUi()
    }

    override fun onStart() {
        super.onStart()
        Cfg.observe(this)
        updateUi()
    }

    override fun onStop() {
        Cfg.removeObserver(this)
        super.onStop()
    }

    override fun onConfigChanged(keys: Set<String>) {
        if (
            Cfg.KEY_DREAM_TEXT_COLOR in keys ||
            Cfg.KEY_DREAM_ACCENT_COLOR in keys
        ) {
            updateUi()
        }
    }

    private fun navigateUp() {
        val navController = runCatching { findNavController() }.getOrNull()
        if (navController?.navigateUp() == true) {
            return
        }

        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun setDreamTextColor(color: Int?) {
        Cfg.edit(requireContext()) {
            Cfg.dreamTextColor = color
        }
    }

    private fun setDreamAccentColor(color: Int?) {
        Cfg.edit(requireContext()) {
            Cfg.dreamAccentColor = color
        }
    }

    private fun showColorPicker(
        initialColor: Int,
        onColorPicked: (Int?) -> Unit,
    ) {
        AmbilWarnaDialog(
            requireContext(),
            initialColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) = Unit

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    onColorPicked(color)
                }
            },
        ).show()
    }

    private fun updateUi() {
        val context = requireContext()
        bindColorPreference(
            summaryView = viewBinding.textColorSummaryTextView,
            swatchView = viewBinding.textColorSwatchView,
            resetButton = viewBinding.textColorResetButton,
            overrideColor = Cfg.dreamTextColor,
        )
        bindColorPreference(
            summaryView = viewBinding.accentColorSummaryTextView,
            swatchView = viewBinding.accentColorSwatchView,
            resetButton = viewBinding.accentColorResetButton,
            overrideColor = Cfg.dreamAccentColor,
        )
    }

    private fun bindColorPreference(
        summaryView: TextView,
        swatchView: View,
        resetButton: MaterialButton,
        overrideColor: Int?,
    ) {
        summaryView.text = if (overrideColor == null) {
            getString(R.string.settings_follow_system_theme)
        } else {
            DreamAppearance.formatColor(overrideColor)
        }
        swatchView.visibility = if (overrideColor == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
        if (overrideColor != null) {
            bindSwatch(swatchView, overrideColor)
        }
        resetButton.isEnabled = overrideColor != null
    }

    private fun bindSwatch(view: View, color: Int) {
        val outlineColor = MaterialColors.getColor(
            view,
            com.google.android.material.R.attr.colorOutline,
        )
        val cornerRadius = 999f * view.resources.displayMetrics.density
        val strokeWidth = view.resources.displayMetrics.density.toInt()
            .coerceAtLeast(1)
        view.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            this.cornerRadius = cornerRadius
            setColor(color)
            setStroke(strokeWidth, outlineColor)
        }
    }
}
