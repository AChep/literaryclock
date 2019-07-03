package com.artemchep.literaryclock.analytics.firebase

import androidx.core.os.bundleOf
import com.artemchep.literaryclock.analytics.AnalyticsAbout
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * @author Artem Chepurnoy
 */
class FirebaseAnalyticsAbout(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsAbout {

    override fun logInstagramOpen() = logWebsiteOpen("instagram")

    override fun logTwitterOpen() = logWebsiteOpen("twitter")

    override fun logLinkedInOpen() = logWebsiteOpen("linkedin")

    override fun logGitHubOpen() = logWebsiteOpen("github")

    private fun logWebsiteOpen(domain: String) {
        val bundle = bundleOf(
            FirebaseAnalytics.Param.ITEM_CATEGORY to FirebaseAnalyticsContract.VIEW_ITEM_CATEGORY_WEBSITE,
            FirebaseAnalytics.Param.ITEM_ID to domain,
            FirebaseAnalytics.Param.ITEM_NAME to domain
        )
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

}