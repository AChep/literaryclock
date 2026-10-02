package com.artemchep.literaryclock.billing

import android.util.Base64
import com.android.billingclient.api.Purchase
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

fun interface PurchaseVerifier {
    fun verify(purchase: Purchase): Boolean
}

/** Verifies the Play signature of a purchase against the app's licensing key. */
class PlayPurchaseVerifier(private val publicKey: String) : PurchaseVerifier {
    private val key: PublicKey? by lazy {
        if (publicKey.isBlank()) return@lazy null
        try {
            KeyFactory.getInstance("RSA").generatePublic(
                X509EncodedKeySpec(Base64.decode(publicKey, Base64.DEFAULT)),
            )
        } catch (_: GeneralSecurityException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    override fun verify(purchase: Purchase): Boolean {
        val key = key ?: return false
        if (purchase.originalJson.isBlank() || purchase.signature.isBlank()) {
            return false
        }
        return try {
            Signature.getInstance("SHA1withRSA").run {
                initVerify(key)
                update(purchase.originalJson.toByteArray(Charsets.UTF_8))
                verify(Base64.decode(purchase.signature, Base64.DEFAULT))
            }
        } catch (_: GeneralSecurityException) {
            false
        } catch (_: IllegalArgumentException) {
            false
        }
    }
}
