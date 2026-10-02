package com.artemchep.literaryclock.billing

import android.util.Base64
import com.android.billingclient.api.Purchase
import com.google.common.truth.Truth.assertThat
import java.security.KeyPairGenerator
import java.security.Signature
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlayPurchaseVerifierTest {
    private val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    private val publicKey = Base64.encodeToString(keyPair.public.encoded, Base64.NO_WRAP)
    private val json = """{"productId":"donation_1","purchaseToken":"test-token","purchaseState":0}"""

    @Test
    fun acceptsValidSignatureAndRejectsModifiedPurchase() {
        val signature = Signature.getInstance("SHA1withRSA").run {
            initSign(keyPair.private)
            update(json.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(sign(), Base64.NO_WRAP)
        }
        val verifier = PlayPurchaseVerifier(publicKey)
        assertThat(verifier.verify(Purchase(json, signature))).isTrue()
        assertThat(verifier.verify(Purchase(json.replace("donation_1", "donation_20"), signature))).isFalse()
    }

    @Test
    fun malformedKeysAndSignaturesFailWithoutCrashing() {
        assertThat(PlayPurchaseVerifier("debug").verify(Purchase(json, "invalid"))).isFalse()
        assertThat(PlayPurchaseVerifier(publicKey).verify(Purchase(json, "%%%"))).isFalse()
        assertThat(PlayPurchaseVerifier(publicKey).verify(Purchase(json, ""))).isFalse()
        assertThat(PlayPurchaseVerifier("").verify(Purchase(json, "invalid"))).isFalse()
    }
}
