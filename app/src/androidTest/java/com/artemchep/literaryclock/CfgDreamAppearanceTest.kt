package com.artemchep.literaryclock

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CfgDreamAppearanceTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun dreamTextAndAccentColorValuesRoundTripThroughCfg() {
        val originalText = Cfg.dreamTextColor
        val originalAccent = Cfg.dreamAccentColor
        val textValue = 0xFF112233.toInt()
        val accentValue = 0xFF445566.toInt()

        try {
            Cfg.edit(context) {
                Cfg.dreamTextColor = textValue
                Cfg.dreamAccentColor = accentValue
            }

            assertEquals(textValue, Cfg.dreamTextColor)
            assertEquals(accentValue, Cfg.dreamAccentColor)

            Cfg.edit(context) {
                Cfg.dreamTextColor = null
                Cfg.dreamAccentColor = null
            }

            assertNull(Cfg.dreamTextColor)
            assertNull(Cfg.dreamAccentColor)
        } finally {
            Cfg.edit(context) {
                Cfg.dreamTextColor = originalText
                Cfg.dreamAccentColor = originalAccent
            }
        }
    }
}
