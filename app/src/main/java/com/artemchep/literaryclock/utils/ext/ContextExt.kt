package com.artemchep.literaryclock.utils.ext

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.artemchep.literaryclock.R

fun Fragment.startActivityIfExists(intent: Intent) =
    handleActivityNotFoundException(requireContext()) {
        startActivity(intent)
    }

fun Context.startActivityIfExists(intent: Intent) =
    handleActivityNotFoundException(this) {
        startActivity(intent)
    }

private inline fun handleActivityNotFoundException(context: Context, block: () -> Unit) {
    try {
        block()
    } catch (e: ActivityNotFoundException) {
        val msg = context.getString(R.string.error_activity_not_found)
        Toast
            .makeText(context, msg, Toast.LENGTH_SHORT)
            .show()
    }
}
