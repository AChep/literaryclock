package com.artemchep.literaryclock.ui

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import com.artemchep.literaryclock.models.Time

fun Context.showTimePickerDialog(time: Time = Time(0), onTimePick: (Time) -> Unit) {
    val innerCallback = TimePickerDialog.OnTimeSetListener { _, hours, minutes ->
        val new = hours * 60 + minutes
        onTimePick.invoke(Time(new))
    }

    val h = time.time / 60
    val m = time.time % 60
    TimePickerDialog(this, innerCallback, h, m, DateFormat.is24HourFormat(this))
        .show()
}
