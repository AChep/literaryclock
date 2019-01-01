package com.artemchep.literaryclock.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.messageLiveEvent
import com.artemchep.literaryclock.models.MessageType
import com.artemchep.literaryclock.startUpdateDatabaseJob
import es.dmoral.toasty.Toasty

/**
 * @author Artem Chepurnoy
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Show all the messages as
        // toasts.
        messageLiveEvent.observe(this, Observer {
            val context = this@MainActivity
            val text = it.text.invoke(context)
            when (it.type) {
                MessageType.SUCCESS -> Toasty.success(context, text)
                MessageType.NORMAL -> Toasty.normal(context, text)
                MessageType.ERROR -> Toasty.error(context, text)
            }
        })

        // Check the database for updates
        // every day.
        startUpdateDatabaseJob(Heart.UID_DATABASE_UPDATE_JOB)
    }

}