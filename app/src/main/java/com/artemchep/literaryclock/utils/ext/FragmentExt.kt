package com.artemchep.literaryclock.utils.ext

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

fun Fragment.setOnApplyWindowInsetsListener(listener: (WindowInsetsCompat) -> WindowInsetsCompat) {
    val v = view ?: return
    ViewCompat.setOnApplyWindowInsetsListener(v) { _, insets ->
        listener(insets)
    }
    ViewCompat.requestApplyInsets(v)
}
