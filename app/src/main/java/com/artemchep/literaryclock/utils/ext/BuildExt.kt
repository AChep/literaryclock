package com.artemchep.literaryclock.utils.ext

import com.artemchep.literaryclock.BuildConfig

/**
 * Executes the block if this build is
 * debug.
 */
inline fun ifDebug(crossinline block: () -> Unit) {
    if (BuildConfig.DEBUG) {
        block()
    }
}
