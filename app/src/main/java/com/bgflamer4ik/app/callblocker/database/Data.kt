package com.bgflamer4ik.app.callblocker.database

import kotlinx.serialization.Serializable

@Serializable
data class NumberData(
    val number: String,
    val hasPattern: Boolean = false
) {
    fun toNumber(): String? {
        return if (!hasPattern) number else null
    }
}

@Serializable
data class DataContainer(
    val blacklist: List<NumberData>,
    val whitelist: List<NumberData>
)

data class KeyData(
    val name: String,
    val data: String
)
data class HistoryData(
    val number: String,
    val block: Boolean,
    val params: Int
)

object DataKeys {
    const val DATA_BLOCK_UNDEFINED = "block_undefined"
    const val DATA_BLOCK_ALL = "block_all"
    const val DATA_SKIP_CALL_LOG = "skip_call_log"
    const val DATA_SKIP_NOTIFICATION = "skip_notification"
    const val BLACK_LIST_KEY = "blacklist"
    const val WHITE_LIST_KEY = "whitelist"
    const val FIRST_LAUNCH_KEY = "first_launch"
}