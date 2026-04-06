package com.artemchep.literaryclock.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.artemchep.literaryclock.R
import com.artemchep.literaryclock.ui.fragments.DreamSettingsFragment

class DreamSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dream_settings)

        if (savedInstanceState != null) {
            return
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, DreamSettingsFragment())
            .commit()
    }
}
