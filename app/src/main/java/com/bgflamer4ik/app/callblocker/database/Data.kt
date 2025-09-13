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
    const val dataBlockUndefined = "block_undefined"
    const val dataBlockAll = "block_all"
    const val dataSkipCallLog = "skip_call_log"
    const val dataSkipNotification = "skip_notification"
    const val blackListKey = "blacklist"
    const val whitelistKey = "whitelist"
    const val firstLaunch = "first_launch"
}