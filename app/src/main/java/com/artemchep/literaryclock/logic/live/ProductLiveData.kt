package com.artemchep.literaryclock.logic.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.literaryclock.listOfSkus
import com.artemchep.literaryclock.models.Loader
import org.solovyev.android.checkout.Checkout
import org.solovyev.android.checkout.Inventory
import org.solovyev.android.checkout.ProductTypes
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Artem Chepurnoy
 */
class ProductLiveData(
    private val checkoutLiveData: LiveData<out Checkout>
) : MediatorLiveData<Loader<Inventory.Products>>() {

    private val loadGeneration = AtomicInteger()

    init {
        addSource(checkoutLiveData) {
            // We assume that the checkout live data
            // immediately returns the checkout object
            // once we've subscribed to it.
            loadInventory(it)
        }
    }

    fun loadInventory(checkout: Checkout = checkoutLiveData.value!!) {
        val generation = loadGeneration.incrementAndGet()
        postValue(Loader.Loading())

        val request = Inventory.Request.create().apply {
            loadAllPurchases()
            loadSkus(ProductTypes.IN_APP, listOfSkus())
        }
        checkout.loadInventory(request) {
            if (generation == loadGeneration.get()) {
                postValue(Loader.Ok(it))
            }
        }
    }

}
