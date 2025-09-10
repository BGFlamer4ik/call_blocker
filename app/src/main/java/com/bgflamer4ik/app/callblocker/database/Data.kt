package com.bgflamer4ik.app.callblocker.database

data class NumberData(
    val number: String,
    val hasPattern: Boolean = false
) {
    fun toNumber(): String? {
        return if (!hasPattern) number else null
    }
}

data class KeyData(
    val name: String,
    val data: String
)
data class HistoryData(
    val number: String,
    val block: Boolean,
    val pattern: Int
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