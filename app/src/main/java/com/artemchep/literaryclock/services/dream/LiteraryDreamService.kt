package com.artemchep.literaryclock.services.dream

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.artemchep.literaryclock.Cfg
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.databinding.DreamMainBinding
import com.artemchep.literaryclock.logic.viewmodels.DreamViewModel
import com.artemchep.literaryclock.models.QuoteItem

/**
 * @author Artem Chepurnoy
 */
class LiteraryDreamService : LifecycleAwareDreamService() {

    val dreamViewModel by lazy { DreamViewModel(application) }

    lateinit var viewBinding: DreamMainBinding

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setContentView(R.layout.dream_main)
        viewBinding = DreamMainBinding.bind(window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0))
        applyAppearance()

        dreamViewModel.setup()
    }

    private fun DreamViewModel.setup() {
        quoteLiveData.observe(lifecycleOwner, Observer(::showQuote))
    }

    private fun showQuote(quote: QuoteItem) = viewBinding.bounceFrameLayout.bounce {
        val context = viewBinding.quoteTextView.context
        viewBinding.quoteTextView.text = quote.quote(
            context = context,
            accentColor = DreamAppearance.resolveEffectiveAccentColor(),
        )
        viewBinding.titleTextView.text = quote.title
        viewBinding.authorTextView.text = quote.author
    }

    private fun applyAppearance() {
        val textColor = Cfg.dreamTextColor ?: return
        viewBinding.quoteTextView.setTextColor(textColor)
        viewBinding.titleTextView.setTextColor(textColor)
        viewBinding.authorTextView.setTextColor(textColor)
    }

}
