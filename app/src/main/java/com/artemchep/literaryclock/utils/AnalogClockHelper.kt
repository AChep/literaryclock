package com.artemchep.literaryclock.utils

fun calculateHourHandRotation(time: Int) = (time / 2f) % 360f

fun calculateMinuteHandRotation(time: Int) = (time % 60f) * 6f
