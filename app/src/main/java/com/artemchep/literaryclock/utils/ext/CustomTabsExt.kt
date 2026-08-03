package com.artemchep.literaryclock.utils.ext

import android.app.Activity
import android.content.ActivityNotFoundException
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.artemchep.literaryclock.R

fun Uri.launchInCustomTabs(activity: Activity) {
    try {
        CustomTabsIntent.Builder().build().launchUrl(activity, this)
    } catch (e: ActivityNotFoundException) {
        Toast
            .makeText(activity, R.string.error_activity_not_found, Toast.LENGTH_SHORT)
            .show()
    }
}
