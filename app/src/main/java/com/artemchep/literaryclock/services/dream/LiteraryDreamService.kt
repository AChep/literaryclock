package com.artemchep.literaryclock.services.dream

import android.view.View
import androidx.lifecycle.Observer
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.logic.viewmodels.DreamViewModel
import com.artemchep.literaryclock.models.QuoteItem
import com.artemchep.literaryclock.startUpdateDatabaseJob
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.dream_main.*

/**
 * @author Artem Chepurnoy
 */
class LiteraryDreamService : LifecycleAwareDreamService(), LayoutContainer {

    override val containerView: View?
        get() = window.decorView

    val dreamViewModel by lazy { DreamViewModel(application) }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setContentView(R.layout.dream_main)

        // Check the database for updates
        // every day.
        startUpdateDatabaseJob(Heart.UID_DATABASE_UPDATE_JOB)

        dreamViewModel.setup()
    }

    private fun DreamViewModel.setup() {
        quoteLiveData.observe(lifecycleOwner, Observer(::showQuote))
    }

    private fun showQuote(quote: QuoteItem) = bounceFrameLayout.bounce {
        quoteTextView.text = quote.quote
        titleTextView.text = quote.title
        authorTextView.text = quote.author
    }

}
