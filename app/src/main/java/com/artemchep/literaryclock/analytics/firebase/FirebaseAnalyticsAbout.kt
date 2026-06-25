package com.artemchep.literaryclock.analytics.firebase

import android.os.Bundle
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
        val bundle = Bundle().apply {
            putString(
                FirebaseAnalytics.Param.ITEM_CATEGORY,
                FirebaseAnalyticsContract.VIEW_ITEM_CATEGORY_WEBSITE
            )
            putString(FirebaseAnalytics.Param.ITEM_ID, domain)
            putString(FirebaseAnalytics.Param.ITEM_NAME, domain)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

}
