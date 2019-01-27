package com.artemchep.literaryclock.ui

import android.view.View
import androidx.core.view.isVisible

fun View.setProgressBarShown(isShown: Boolean) {
    isVisible = true
    animate().cancel() // cancel previous animation
    animate()
        .apply {
            when (isShown) {
                true -> scaleY(1f).alpha(1f)
                false -> scaleY(0f)
                    .alpha(0f)
                    .withEndAction {
                        isVisible = false
                    }
            }
        }
}
