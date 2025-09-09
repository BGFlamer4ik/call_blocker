package com.bgflamer4ik.app.callblocker.database

import android.content.Context

object DBHelper {
    fun patternDecrypt(p0: Int): String {
        return when(p0) {
            0 -> "Number passed"
            1 -> "Number in Black list"
            2 -> "Number matches pattern in Blacklist"
            3 -> "Number in White list"
            4 -> "Number matches pattern in Whitelist"
            5 -> "Number not passed. Block All."
            else -> "Error?"
        }
    }

    fun updateKeys(context: Context) {
        val db = DBRepository(context)
        val keys = arrayOf(
            DataKeys.dataBlockAll,
            DataKeys.dataBlockUndefined,
            DataKeys.dataSkipNotification,
            DataKeys.dataSkipCallLog
        )
        keys.forEach {
            if (db.getKeySync(it) == null) {
                db.add(KeyData(it, "false"))
            }
        }
    }
}