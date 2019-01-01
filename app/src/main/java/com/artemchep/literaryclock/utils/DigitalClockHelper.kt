package com.artemchep.literaryclock.utils

import android.content.Context
import com.artemchep.literaryclock.models.Time
import java.text.DateFormat
import java.util.*

fun formatTime(time: Time, format: DateFormat): String = Calendar.getInstance()
    .apply {
        val h = time.time / 60
        val m = time.time % 60
        set(Calendar.HOUR_OF_DAY, h)
        set(Calendar.MINUTE, m)
    }.time.let(format::format)

fun createTimeFormat(context: Context): DateFormat =
    android.text.format.DateFormat.getTimeFormat(context)
