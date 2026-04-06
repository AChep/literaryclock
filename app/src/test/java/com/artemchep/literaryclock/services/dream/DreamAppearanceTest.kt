package com.artemchep.literaryclock.services.dream

import org.junit.Assert.assertEquals
import org.junit.Test

class DreamAppearanceTest {
    @Test
    fun formatColorReturnsUppercaseArgbHex() {
        assertEquals("#FF1234AB", DreamAppearance.formatColor(0xFF1234AB.toInt()))
    }
}
