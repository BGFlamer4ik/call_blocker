package com.bgflamer4ik.app.callblocker.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.core.content.ContextCompat
import com.bgflamer4ik.app.callblocker.database.DBRepository
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.database.HistoryData
import com.bgflamer4ik.app.callblocker.database.HistoryDataParams
import com.bgflamer4ik.app.callblocker.database.NumberData

object CallBlockerMatchHelper {
    fun shouldBlockNumber(context: Context, handle: Uri): Boolean {
        val db = DBRepository(context)
        val number = getPhoneNumberHandle(context, handle)
        val isBlockUndefined = db.getKeySync(DataKeys.DATA_BLOCK_UNDEFINED) == "true"

        if (number.isNullOrEmpty()) {
            addToHistory(db,"$number", isBlockUndefined, HistoryDataParams.UNDEFINED)
            return isBlockUndefined
        }

        if (isInContacts(context, handle) == false && isBlockUndefined) {
            addToHistory(db, number, true, HistoryDataParams.UNDEFINED)
            return true
        }

        val black = db.getNumbersSync(DataKeys.BLACK_LIST_KEY)
        val white = db.getNumbersSync(DataKeys.WHITE_LIST_KEY)

        if (black.map { it.toNumber() }.contains(number)) {
            addToHistory(db,number, true, HistoryDataParams.BLACK)
            return true
        }
        if (white.map { it.toNumber() }.contains(number)) {
            addToHistory(db, number, false, HistoryDataParams.WHITE)
            return false
        }

        if (black.any { isMatchesNumber(number, it) }) {
            addToHistory(db, number, true, HistoryDataParams.BLACK_PATTERN)
            return true
        }
        if (white.any { isMatchesNumber(number, it) }) {
            addToHistory(db, number, false, HistoryDataParams.WHITE_PATTERN)
            return false
        }
        if (db.getKeySync(DataKeys.DATA_BLOCK_ALL) == "true") {
            addToHistory(db, number, true, HistoryDataParams.BLOCK_ALL)
            return true
        }
        addToHistory(db, number, false, HistoryDataParams.PASS)
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

    fun getPhoneNumberHandle(context: Context, handle: Uri): String? {
        return when (handle.scheme) {
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
                Log.d( "CallBlockingService", "Unknown scheme: ${handle.scheme}")
                null
            }
        }
    }

    fun isInContacts(context: Context, handle: Uri): Boolean? {
        if (DBRepository(context).getKeySync(DataKeys.DATA_BLOCK_UNDEFINED) == "true") {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                context.contentResolver.query(
                    handle,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    ),
                    null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        return !cursor.getString(
                            cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        ).isNullOrBlank()
                    }
                }
                return false
            } else return null
        }
        return null
    }
}