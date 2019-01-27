package com.artemchep.literaryclock.data

/**
 * @author Artem Chepurnoy
 */
enum class DatabaseState(val isWorking: Boolean) {
    IDLE(false),
    UPDATING(true),
}
