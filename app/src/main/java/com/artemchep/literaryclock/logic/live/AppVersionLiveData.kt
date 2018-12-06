package com.artemchep.literaryclock.logic.live

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import com.artemchep.literaryclock.models.Version

/**
 * @author Artem Chepurnoy
 */
class AppVersionLiveData(private val context: Context) : LiveData<Version>() {

    override fun onActive() {
        super.onActive()

        val packageInfo: PackageInfo
        try {
            packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }

        postVersion(packageInfo)
    }

    private fun postVersion(packageInfo: PackageInfo) =
        Version(
            versionName = packageInfo.versionName!!
        ).let(::setValue)

}