package com.artemchep.literaryclock.ui

import androidx.fragment.app.FragmentManager
import com.artemchep.literaryclock.models.Time
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

fun FragmentManager.showTimePickerDialog(
    tag: String,
    time: Time = Time(0),
    onTimePick: (Time) -> Unit,
) {
    if (findFragmentByTag(tag) != null) return
    val h = time.time / 60
    val m = time.time % 60
    val picker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .setHour(h)
        .setMinute(m)
        .build()
    picker.setTimePickListener(onTimePick)
    picker.show(this, tag)
}

fun FragmentManager.restoreTimePickerDialog(tag: String, onTimePick: (Time) -> Unit) {
    (findFragmentByTag(tag) as? MaterialTimePicker)?.setTimePickListener(onTimePick)
}

private fun MaterialTimePicker.setTimePickListener(onTimePick: (Time) -> Unit) {
    clearOnPositiveButtonClickListeners()
    addOnPositiveButtonClickListener {
        val new = hour * 60 + minute
        onTimePick.invoke(Time(new))
    }
}
