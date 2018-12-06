package com.artemchep.literaryclock.utils.ext

import android.app.Activity
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun Uri.launchInCustomTabs(activity: Activity) {
    CustomTabsIntent.Builder().build().launchUrl(activity, this)
}
