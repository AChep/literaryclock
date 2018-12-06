package com.artemchep.literaryclock.utils

import com.artemchep.literaryclock.models.Time
import java.util.*

val currentTime: Time
    get() = Calendar.getInstance().run {
        get(Calendar.MINUTE) + get(Calendar.HOUR_OF_DAY) * 60
    }.let(::Time)
