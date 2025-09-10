package com.bgflamer4ik.app.callblocker

import com.bgflamer4ik.app.callblocker.database.NumberData

object SettingsHelper {
    fun numberCorrector(number: String): NumberData {
        var tmp = if (number.startsWith("+") || number.startsWith("*")) {
            number
        } else if (number.isNotEmpty()) {
            "*$number"
        } else {
            ""
        }

        tmp = tmp.replace(Regex("[()\\s-\\p{L}]"), "")
        val isPattern = number.contains(Regex("[^0-9+]"))
        if (isPattern) {
            if (tmp.contains("[") && !tmp.contains("]")) {
                tmp += "]"
            }
        }

        return NumberData(tmp, isPattern)
    }
}