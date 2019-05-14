package com.artemchep.literaryclock.logic.live

import androidx.lifecycle.LiveData
import com.artemchep.config.Config
import com.artemchep.literaryclock.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigLiveData<T>(private val key: String, private val getter: (Cfg) -> T) : LiveData<T>() {

    private val cfgObserver = object : Config.OnConfigChangedListener<String> {
        override fun onConfigChanged(keys: Set<String>) {
            if (key in keys) {
                updateValue()
            }
        }
    }

    override fun onActive() {
        super.onActive()
        Cfg.observe(cfgObserver)
        updateValue()
    }

    override fun onInactive() {
        Cfg.removeObserver(cfgObserver)
        super.onInactive()
    }

    private fun updateValue() {
        val v = getter.invoke(Cfg)
        postValue(v)
    }

}
