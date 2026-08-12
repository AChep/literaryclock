package com.artemchep.literaryclock.receivers

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.artemchep.literaryclock.Heart
import com.artemchep.literaryclock.data.room.LiteraryClockDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.direct
import org.kodein.di.instance
import org.robolectric.RobolectricTestRunner
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@RunWith(RobolectricTestRunner::class)
class WidgetUpdateReceiverTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var database: LiteraryClockDatabase

    @Before
    fun setUp() = runBlocking {
        val app = context.applicationContext as Heart
        database = app.di.direct.instance()
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

    @After
    fun tearDown() = runBlocking {
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

    @Test
    fun staleFavoriteQuoteKeyDoesNotCrashReceiver() = runBlocking {
        val intent = Intent(context, WidgetUpdateReceiver::class.java).apply {
            action = Heart.ACTION_TOGGLE_WIDGET_FAVORITE
            putExtra(WidgetUpdateReceiver.EXTRA_QUOTE_KEY, "stale-quote-key")
        }

        WidgetUpdateReceiver().invokeToggleFavorite(context, intent)

        val isFavorite = withContext(Dispatchers.IO) {
            database.literaryClockDao().isFavoriteQuote("stale-quote-key")
        }
        assertThat(isFavorite).isFalse()
    }

    private suspend fun WidgetUpdateReceiver.invokeToggleFavorite(
        context: Context,
        intent: Intent,
    ) {
        suspendCoroutine<Unit> { continuation ->
            val method = WidgetUpdateReceiver::class.java.getDeclaredMethod(
                "toggleFavorite",
                Context::class.java,
                Intent::class.java,
                Continuation::class.java,
            )
            method.isAccessible = true
            val result = try {
                method.invoke(this, context, intent, continuation)
            } catch (e: InvocationTargetException) {
                continuation.resumeWithException(e.targetException)
                return@suspendCoroutine
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
                return@suspendCoroutine
            }
            if (result !== COROUTINE_SUSPENDED) {
                continuation.resume(Unit)
            }
        }
    }
}
