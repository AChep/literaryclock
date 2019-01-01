package com.artemchep.literaryclock.models

import android.content.Context

/**
 * @author Artem Chepurnoy
 */
data class Message(
    val type: MessageType,
    val text: Context.() -> String
)
