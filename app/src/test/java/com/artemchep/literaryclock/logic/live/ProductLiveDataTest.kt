package com.artemchep.literaryclock.logic.live

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.artemchep.literaryclock.models.Loader
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.solovyev.android.checkout.Checkout
import org.solovyev.android.checkout.Inventory

@RunWith(RobolectricTestRunner::class)
class ProductLiveDataTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun olderInventoryLoadDoesNotOverwriteNewerResult() {
        val checkout = mock<Checkout>()
        val callbacks = mutableListOf<Inventory.Callback>()
        whenever(checkout.loadInventory(any(), any())).thenAnswer { invocation ->
            callbacks += invocation.getArgument<Inventory.Callback>(1)
            mock<Inventory>()
        }
        val productLiveData = ProductLiveData(MutableLiveData(checkout))
        productLiveData.observeForever { }

        productLiveData.loadInventory(checkout)

        val newerProducts = mock<Inventory.Products>()
        callbacks[1].onLoaded(newerProducts)
        val olderProducts = mock<Inventory.Products>()
        callbacks[0].onLoaded(olderProducts)

        val value = productLiveData.value
        assertThat(value).isInstanceOf(Loader.Ok::class.java)
        assertThat((value as Loader.Ok).value).isSameInstanceAs(newerProducts)
    }
}
