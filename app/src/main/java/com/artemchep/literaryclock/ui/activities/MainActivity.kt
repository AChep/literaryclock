package com.artemchep.literaryclock.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.messageLiveEvent
import com.artemchep.literaryclock.startUpdateDatabaseJob

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
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        })

        // Check the database for updates
        // every day.
        startUpdateDatabaseJob(Heart.UID_DATABASE_UPDATE_JOB)
    }

}