package com.artemchep.literaryclock.models

/**
 * @author Artem Chepurnoy
 */
inline class Time(val time: Int) : Comparable<Time> {

    override fun compareTo(other: Time): Int = time.compareTo(other.time)

}
