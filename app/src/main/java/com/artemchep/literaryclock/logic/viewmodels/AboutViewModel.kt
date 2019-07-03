package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import com.artemchep.literaryclock.analytics.AnalyticsAbout
import com.artemchep.literaryclock.logic.SingleLiveEvent
import com.artemchep.literaryclock.logic.live.AppDependencyLiveData
import com.artemchep.literaryclock.logic.live.AppVersionLiveData
import org.kodein.di.generic.instance

/**
 * @author Artem Chepurnoy
 */
class AboutViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val URL_AUTHOR_INSTAGRAM = "https://www.instagram.com/artemchep"
        const val URL_AUTHOR_TWITTER = "https://twitter.com/ArtemChep"
        const val URL_AUTHOR_LINKEDIN = "https://www.linkedin.com/in/artemchep"
        const val URL_APP_GITHUB = "https://github.com/AChep/literaryclock"
    }

    private val analytics by instance<AnalyticsAbout>()

    val openUrlEvent = SingleLiveEvent<String>()

    val depsLiveData = AppDependencyLiveData()

    val versionLiveData = AppVersionLiveData(application)

    @UiThread
    fun openTwitter() {
        openUrl(URL_AUTHOR_TWITTER)
        analytics.logTwitterOpen()
    }

    @UiThread
    fun openInstagram() {
        openUrl(URL_AUTHOR_INSTAGRAM)
        analytics.logInstagramOpen()
    }

    @UiThread
    fun openLinkedIn() {
        openUrl(URL_AUTHOR_LINKEDIN)
        analytics.logLinkedInOpen()
    }

    @UiThread
    fun openGitHub() {
        openUrl(URL_APP_GITHUB)
        analytics.logGitHubOpen()
    }

    @UiThread
    private fun openUrl(url: String) = url.also(openUrlEvent::setValue)

}
