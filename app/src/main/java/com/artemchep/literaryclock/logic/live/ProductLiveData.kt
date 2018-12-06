package com.artemchep.literaryclock.logic.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.literaryclock.listOfSkus
import org.solovyev.android.checkout.Checkout
import org.solovyev.android.checkout.Inventory
import org.solovyev.android.checkout.ProductTypes

/**
 * @author Artem Chepurnoy
 */
class ProductLiveData(
    private val checkoutLiveData: LiveData<out Checkout>
) : MediatorLiveData<Inventory.Products>() {

    init {
        addSource(checkoutLiveData) {
            // We assume that the checkout live data
            // immediately returns the checkout object
            // once we've subscribed to it.
        }
    }

    private val inventoryCallback = Inventory.Callback(::postValue)

    override fun onActive() {
        super.onActive()
        loadInventory()
    }

    private fun loadInventory() {
        val request = Inventory.Request.create().apply {
            loadAllPurchases()
            loadSkus(ProductTypes.IN_APP, listOfSkus())
        }

        val checkout = checkoutLiveData.value!!
        checkout.loadInventory(request, inventoryCallback)
    }

}