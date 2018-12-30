package com.artemchep.literaryclock

import com.artemchep.config.common.SharedPrefConfig

/**
 * @author
 */
object CfgInternal : SharedPrefConfig("state") {

    const val KEY_SYNC_TIMESTAMP = "sync_timestamp"

    var syncTimestamp by configDelegate(KEY_SYNC_TIMESTAMP, 0L)

}