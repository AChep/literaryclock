package com.artemchep.literaryclock.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.startUpdateDatabaseJob

/**
 * @author Artem Chepurnoy
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check the database for updates
        // every day.
        startUpdateDatabaseJob(Heart.UID_DATABASE_UPDATE_JOB)
    }

}