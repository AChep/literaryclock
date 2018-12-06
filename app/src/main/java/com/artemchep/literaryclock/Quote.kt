package com.artemchep.literaryclock

/**
 * @author Artem Chepurnoy
 */
data class Quote(
    /**
     * The quote from a book.
     */
    val quote: String,
    val title: String,
    val author: String,
    val asin: String
)
