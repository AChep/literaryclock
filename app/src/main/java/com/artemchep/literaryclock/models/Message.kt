package com.artemchep.literaryclock.models

import android.content.Context
import androidx.annotation.StringRes

/**
 * @author Artem Chepurnoy
 */
data class Message(
    val type: MessageType,
    val text: Context.() -> String
)

//
// Message DSL
//

fun message(builder: MessageBuilder.() -> Unit): Message = MessageBuilder().apply(builder).build()

class MessageBuilder internal constructor() {

    var type: MessageType = MessageType.NORMAL

    var text: Context.() -> String = { error("") }

    fun setTextStringRes(@StringRes stringRes: Int) {
        text = { getString(stringRes) }
    }

    fun build(): Message = Message(
        type = type,
        text = text
    )

}
