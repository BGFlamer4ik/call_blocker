package com.bgflamer4ik.app.callblocker

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.sqlite.transaction
import com.bgflamer4ik.app.callblocker.database.DBCore
import com.bgflamer4ik.app.callblocker.database.DataContainer
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.database.NumberData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.InputStreamReader

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

    fun exportNumbersList(context: Context, fileName: String = "exported_database.json"): Boolean {
        val db = DBCore(context).readableDatabase
        val gson = GsonBuilder().setPrettyPrinting().create()

        fun fetchTable(table: String): List<NumberData> {
            val cursor = db.rawQuery("SELECT * FROM $table", null)
            val list = mutableListOf<NumberData>()
            while (cursor.moveToNext()) {
                val number = cursor.getString(cursor.getColumnIndexOrThrow("number"))
                val hasPattern = cursor.getString(cursor.getColumnIndexOrThrow("hasPattern"))
                list.add(NumberData(number, hasPattern == "true"))
            }
            cursor.close()
            return list
        }

        val exportData = DataContainer(
            blacklist = fetchTable(DataKeys.BLACK_LIST_KEY),
            whitelist = fetchTable(DataKeys.WHITE_LIST_KEY)
        )

        db.close()
        val jsonString = gson.toJson(exportData)
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/json")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri = resolver.insert(collection, contentValues)

        if (itemUri == null) {
            return false
        }

        try {
            resolver.openOutputStream(itemUri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray())
                outputStream.flush()
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(itemUri, contentValues, null, null)

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun importNumberList(context: Context, uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val gson = Gson()
            val data = gson.fromJson(reader, DataContainer::class.java)
            reader.close()

            val db = DBCore(context).writableDatabase
            db.transaction {
                try {
                    execSQL("DELETE FROM ${DataKeys.BLACK_LIST_KEY}")
                    execSQL("DELETE FROM ${DataKeys.WHITE_LIST_KEY}")

                    fun insertList(table: String, list: List<NumberData>) {
                        for (item in list) {
                            val cv = ContentValues().apply {
                                put("number", item.number)
                                put("hasPattern", item.hasPattern)
                            }
                            insert(table, null, cv)
                        }
                    }

                    insertList(DataKeys.BLACK_LIST_KEY, data.blacklist)
                    insertList(DataKeys.WHITE_LIST_KEY, data.whitelist)

                } finally {}
            }
            db.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}