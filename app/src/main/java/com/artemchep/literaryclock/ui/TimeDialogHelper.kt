package com.artemchep.literaryclock.ui

import androidx.fragment.app.FragmentManager
import com.artemchep.literaryclock.models.Time
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

fun FragmentManager.showTimePickerDialog(time: Time = Time(0), onTimePick: (Time) -> Unit) {
    val h = time.time / 60
    val m = time.time % 60
    val picker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .setHour(h)
        .setMinute(m)
        .build()
    picker.addOnPositiveButtonClickListener {
        val new = picker.hour * 60 + picker.minute
        onTimePick.invoke(Time(new))
    }
    picker.show(this, null)
}
