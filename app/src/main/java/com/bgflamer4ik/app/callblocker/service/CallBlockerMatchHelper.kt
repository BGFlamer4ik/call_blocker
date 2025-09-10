package com.bgflamer4ik.app.callblocker.service

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.bgflamer4ik.app.callblocker.database.DBRepository
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.database.HistoryData
import com.bgflamer4ik.app.callblocker.database.NumberData

object CallBlockerMatchHelper {
    fun shouldBlockNumber(context: Context, number: String?): Boolean {
        val db = DBRepository(context)

        if (number.isNullOrEmpty()) {
            val isNeeded = db.getKeySync(DataKeys.dataBlockUndefined) == "true"
            addToHistory(db,"$number", isNeeded, 10)
            return isNeeded
        }

        val black = db.getNumbersSync(DataKeys.blackListKey)
        val white = db.getNumbersSync(DataKeys.whitelistKey)

        if (black.map { it.toNumber() }.contains(number)) {
            addToHistory(db,number, true, 1)
            return true
        }
        if (white.map { it.toNumber() }.contains(number)) {
            addToHistory(db, number, false, 3)
            return false
        }

        if (black.any { isMatchesNumber(number, it) }) {
            addToHistory(db, number, true, 2)
            return true
        }
        if (white.any { isMatchesNumber(number, it) }) {
            addToHistory(db, number, false, 4)
            return false
        }
        if (db.getKeySync(DataKeys.dataBlockAll) == "true") {
            addToHistory(db, number, true, 5)
            return true
        }
        addToHistory(db, number, false, 0)
        return false
    }

    private fun addToHistory(dbRepository: DBRepository, number: String, isBlocked: Boolean, params: Int) {
        dbRepository.add(HistoryData(
            number = number,
            block = isBlocked,
            params = params
        ))
    }

    private fun isMatchesNumber(number: String, pattern: NumberData): Boolean {
        if (pattern.hasPattern) {
            return try {
                val regex = patternToRegex(pattern.number)
                regex.matches(number)
            } catch (_: Exception) {
                return false
            }
        } else return false
    }

    fun patternToRegex(pattern: String): Regex {
        val escaped = StringBuilder()
        var i = 0

        while (i < pattern.length) {
            val c = pattern[i]
            when(c) {
                '*' -> escaped.append(".*")
                '?' -> escaped.append(".")
                '[' -> {
                    val endIndex = pattern.indexOf(']', i)
                    if (endIndex == -1) throw IllegalArgumentException("Uncorrected mask")
                    val group = pattern.substring(i, endIndex+1)
                    escaped.append(group)
                    i = endIndex
                }
                '.', '(', ')', '+', '^', '$', '{', '}', '|', '\\' -> escaped.append("\\$c")
                else -> escaped.append(c)
            }
            i++
        }
        return Regex("^$escaped$")
    }

    fun getPhoneNumberHandle(context: Context, handle: Uri?): String? {
        return when (handle?.scheme) {
            "tel" -> handle.schemeSpecificPart
            "content" -> {
                context.contentResolver.query(
                    handle,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ),
                    null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    } else null
                }
            }
            else -> {
                Log.d( "CallBlockingService", "Unknown scheme: ${handle?.scheme}")
                null
            }
        }
    }
}