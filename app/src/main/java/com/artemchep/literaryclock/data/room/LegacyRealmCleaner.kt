package com.artemchep.literaryclock.data.room

import android.content.Context
import java.io.File

object LegacyRealmCleaner {
    fun deleteDefaultRealmFiles(context: Context) {
        val filesDir = context.filesDir ?: return
        delete(filesDir.resolve("default.realm"))
        delete(filesDir.resolve("default.realm.lock"))
        delete(filesDir.resolve("default.realm.note"))
        delete(filesDir.resolve("default.realm.management"))
    }

    private fun delete(file: File) {
        if (!file.exists()) return
        if (file.isDirectory) {
            file.listFiles()?.forEach(::delete)
        }
        file.delete()
    }
}
