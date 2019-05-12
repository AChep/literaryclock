package com.artemchep.literaryclock.models

/**
 * @author Artem Chepurnoy
 */
sealed class Loader<T> {
    class Loading<T> : Loader<T>()
    class Ok<T>(val value: T) : Loader<T>()
    class Error<T> : Loader<T>()
}
