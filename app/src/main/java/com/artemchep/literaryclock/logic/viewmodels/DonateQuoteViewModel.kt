package com.artemchep.literaryclock.logic.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.Suggestions
import com.artemchep.literaryclock.data.firestore.FirestoreSuggestionModel
import com.artemchep.literaryclock.logic.SingleLiveEvent
import com.artemchep.literaryclock.logic.live.DatabaseStateLiveData
import com.artemchep.literaryclock.messageLiveEvent
import com.artemchep.literaryclock.models.*
import com.artemchep.literaryclock.utils.currentTime
import com.artemchep.literaryclock.utils.ext.observeOnce
import org.kodein.di.instance

/**
 * @author Artem Chepurnoy
 */
class DonateQuoteViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        const val TAG = "DonateQuoteViewModel"
    }

    val popEvent = SingleLiveEvent<Unit>()

    val editTimeEvent = SingleLiveEvent<Time>()

    val textLiveData = MutableLiveData<String>()

    val timeLiveData = MutableLiveData<Time>()
        .apply {
            value = currentTime
        }

    val databaseIsUpdatingLiveData = DatabaseStateLiveData(application)

    val momentLiveData by instance<LiveData<Time>, LiveData<MomentItem>>(arg = timeLiveData)

    val quoteLiveData by instance<LiveData<MomentItem>, LiveData<QuoteItem>>(arg = momentLiveData)

    @UiThread
    fun postText(text: String) {
        textLiveData.value = text
    }

    @UiThread
    fun postTime(time: Time) {
        timeLiveData.value = time
    }

    @UiThread
    fun editTime() {
        timeLiveData.observeOnce(editTimeEvent::postValue)
    }

    /**
     * Sends current suggestion to a server and pops the
     * navigation stack.
     */
    @UiThread
    fun send() {
        val text = textLiveData.value
        if (text.isNullOrBlank()) {
            // Show error message that the text
            // can not be blank.
            messageLiveEvent.value = message {
                type = MessageType.ERROR
                setTextStringRes(R.string.error_quote_empty)
            }
            return
        }

        timeLiveData.observeOnce { time ->
            val suggestion = FirestoreSuggestionModel(
                text = text,
                time = time.time
            )

            Suggestions.ref.add(suggestion)
                .addOnCompleteListener {
                    messageLiveEvent.value = if (it.isSuccessful) {
                        // Show success message that the suggestion
                        // has been sent.
                        message {
                            type = MessageType.SUCCESS
                            setTextStringRes(R.string.donate_quote__sent)
                        }
                    } else {
                        message {
                            type = MessageType.ERROR
                            setTextStringRes(R.string.donate_quote__failed)
                        }
                    }
                }

            // Pop the navigation stack
            popEvent.call()
        }
    }

}
