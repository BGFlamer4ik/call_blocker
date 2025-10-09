package com.bgflamer4ik.app.callblocker.database

import android.content.Context

object DBHelper {
    fun patternDecrypt(p0: Int): String {
        return when(p0) {
            HistoryDataParams.PASS -> "Number passed"
            HistoryDataParams.BLACK -> "Number in Black list"
            HistoryDataParams.BLACK_PATTERN -> "Number matches pattern in Blacklist"
            HistoryDataParams.WHITE -> "Number in White list"
            HistoryDataParams.WHITE_PATTERN -> "Number matches pattern in Whitelist"
            HistoryDataParams.BLOCK_ALL -> "Number not passed. Block All."
            HistoryDataParams.UNDEFINED -> "Number is undefined."
            else -> "Error?"
        }
    }

    fun updateKeys(context: Context) {
        val db = DBRepository(context)
        val keys = arrayOf(
            DataKeys.DATA_BLOCK_ALL,
            DataKeys.DATA_BLOCK_UNDEFINED,
            DataKeys.DATA_SKIP_NOTIFICATION,
            DataKeys.DATA_SKIP_CALL_LOG,
        )
        if (db.getKeySync(DataKeys.FIRST_LAUNCH_KEY) == null) {
            db.add(KeyData(DataKeys.FIRST_LAUNCH_KEY, "true"))
        }
        keys.forEach {
            if (db.getKeySync(it) == null) {
                db.add(KeyData(it, "false"))
            }
        }
    }
}