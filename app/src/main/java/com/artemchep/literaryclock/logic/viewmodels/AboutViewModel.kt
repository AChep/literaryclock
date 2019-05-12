package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.AndroidViewModel
import com.artemchep.literaryclock.logic.SingleLiveEvent
import com.artemchep.literaryclock.logic.live.AppDependencyLiveData
import com.artemchep.literaryclock.logic.live.AppVersionLiveData
import com.artemchep.literaryclock.models.QuoteItem

/**
 * @author Artem Chepurnoy
 */
class AboutViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val URL_AUTHOR_INSTAGRAM = "https://www.instagram.com/artemchep"
        const val URL_AUTHOR_TWITTER = "https://twitter.com/ArtemChep"
        const val URL_AUTHOR_LINKEDIN = "https://www.linkedin.com/in/artemchep"
        const val URL_APP_GITHUB = "https://github.com/AChep/literaryclock"
    }

    val openUrlEvent = SingleLiveEvent<String>()

    val depsLiveData = AppDependencyLiveData()

    val versionLiveData = AppVersionLiveData(application)

    @UiThread
    fun openTwitter() = openUrl(URL_AUTHOR_TWITTER)

    @UiThread
    fun openInstagram() = openUrl(URL_AUTHOR_INSTAGRAM)

    @UiThread
    fun openLinkedIn() = openUrl(URL_AUTHOR_LINKEDIN)

    @UiThread
    fun openGitHub() = openUrl(URL_APP_GITHUB)

    @UiThread
    private fun openUrl(url: String) = url.also(openUrlEvent::setValue)

}
