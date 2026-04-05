package com.artemchep.config

interface Config<K> {
    interface OnConfigChangedListener<K> {
        fun onConfigChanged(keys: Set<K>)
    }
}
