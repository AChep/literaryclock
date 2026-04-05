package com.artemchep.config.common

import android.content.Context
import android.content.SharedPreferences
import com.artemchep.config.Config
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class SharedPrefConfig(
    private val name: String,
) : Config<String> {
    private lateinit var sharedPreferences: SharedPreferences
    private val listeners = linkedSetOf<Config.OnConfigChangedListener<String>>()

    private var currentEditor: SharedPreferences.Editor? = null
    private var currentChangedKeys: MutableSet<String>? = null

    fun init(context: Context) {
        if (::sharedPreferences.isInitialized) {
            return
        }

        sharedPreferences = context.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun observe(listener: Config.OnConfigChangedListener<String>) {
        listeners += listener
    }

    fun removeObserver(listener: Config.OnConfigChangedListener<String>) {
        listeners -= listener
    }

    fun edit(context: Context, block: SharedPrefConfig.() -> Unit) {
        init(context)
        check(currentEditor == null) { "Nested config edits are not supported." }

        currentEditor = sharedPreferences.edit()
        currentChangedKeys = linkedSetOf()
        try {
            block()
            currentEditor?.apply()
        } finally {
            currentEditor = null
            val changedKeys = currentChangedKeys.orEmpty()
            currentChangedKeys = null
            if (changedKeys.isNotEmpty()) {
                notifyChanged(changedKeys)
            }
        }
    }

    protected fun configDelegate(key: String, defaultValue: Boolean): ReadWriteProperty<Any?, Boolean> =
        primitiveDelegate(
            key = key,
            defaultValue = defaultValue,
            getter = { prefs, prefKey, fallback -> prefs.getBoolean(prefKey, fallback) },
            setter = { editor, prefKey, value -> editor.putBoolean(prefKey, value) },
        )

    protected fun configDelegate(key: String, defaultValue: Long): ReadWriteProperty<Any?, Long> =
        primitiveDelegate(
            key = key,
            defaultValue = defaultValue,
            getter = { prefs, prefKey, fallback -> prefs.getLong(prefKey, fallback) },
            setter = { editor, prefKey, value -> editor.putLong(prefKey, value) },
        )

    protected fun configDelegate(key: String, defaultValue: String): ReadWriteProperty<Any?, String> =
        primitiveDelegate(
            key = key,
            defaultValue = defaultValue,
            getter = { prefs, prefKey, fallback -> prefs.getString(prefKey, fallback) ?: fallback },
            setter = { editor, prefKey, value -> editor.putString(prefKey, value) },
        )

    private fun <T> primitiveDelegate(
        key: String,
        defaultValue: T,
        getter: (SharedPreferences, String, T) -> T,
        setter: (SharedPreferences.Editor, String, T) -> Unit,
    ): ReadWriteProperty<Any?, T> = object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            check(::sharedPreferences.isInitialized) {
                "Config '$name' must be initialized before reading '$key'."
            }

            return getter(sharedPreferences, key, defaultValue)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val editor = checkNotNull(currentEditor) {
                "Config '$name' must be edited via edit(context) before writing '$key'."
            }

            setter(editor, key, value)
            currentChangedKeys?.add(key)
        }
    }

    private fun notifyChanged(keys: Set<String>) {
        listeners.toList().forEach { listener ->
            listener.onConfigChanged(keys)
        }
    }
}
