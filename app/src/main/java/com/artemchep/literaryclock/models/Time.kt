package com.artemchep.literaryclock.models

/**
 * @author Artem Chepurnoy
 */
// TODO: Use Kotlin inline class when
// it will be possible.
data class Time(val time: Int) : Comparable<Time> {

    override fun compareTo(other: Time): Int = time.compareTo(other.time)

}
