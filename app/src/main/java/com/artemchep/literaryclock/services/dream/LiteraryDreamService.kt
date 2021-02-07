package com.artemchep.literaryclock.services.dream

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.databinding.DreamMainBinding
import com.artemchep.literaryclock.logic.viewmodels.DreamViewModel
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.startUpdateDatabaseJob
import kotlinx.android.extensions.LayoutContainer

/**
 * @author Artem Chepurnoy
 */
class LiteraryDreamService : LifecycleAwareDreamService(), LayoutContainer {

    override val containerView: View?
        get() = window.decorView

    val dreamViewModel by lazy { DreamViewModel(application) }

    lateinit var viewBinding: DreamMainBinding

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setContentView(R.layout.dream_main)
        viewBinding = DreamMainBinding.bind(window.decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0))

        // Check the database for updates
        // every day.
        startUpdateDatabaseJob(Heart.UID_DATABASE_UPDATE_JOB)

        dreamViewModel.setup()
    }

    private fun DreamViewModel.setup() {
        quoteLiveData.observe(lifecycleOwner, Observer(::showQuote))
    }

    private fun showQuote(quote: QuoteItem) = viewBinding.bounceFrameLayout.bounce {
        viewBinding.quoteTextView.text = quote.quote
        viewBinding.titleTextView.text = quote.title
        viewBinding.authorTextView.text = quote.author
    }

}
