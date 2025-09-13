package com.bgflamer4ik.app.callblocker

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.bgflamer4ik.app.callblocker.database.DBRepository
import com.bgflamer4ik.app.callblocker.database.DataContainer
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.database.NumberData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
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

    suspend fun exportNumbersList(
        context: Context,
        lists: List<String>
    ): Result<Uri> {
        return withContext(Dispatchers.IO) {
            val db = DBRepository(context)
            val fileName = "exported_base.json"
            val gson = GsonBuilder().setPrettyPrinting().create()

            try {
                val data = DataContainer(
                    blacklist = if (lists.contains(DataKeys.blackListKey))
                        db.getNumbersSync(DataKeys.blackListKey)
                        else listOf(),
                    whitelist = if (lists.contains(DataKeys.whitelistKey))
                        db.getNumbersSync(DataKeys.whitelistKey)
                        else listOf()
                )

                val out = gson.toJson(data).toByteArray()

                val contentResolver = context.contentResolver
                val content = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/")
                }

                val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), content)
                    ?: throw IOException("Cannot create MediaStore file")
                contentResolver.openOutputStream(uri).use {
                    it?.write(out)
                } ?: throw IOException("Cannot open OutputStream for uri: $uri")
                Result.success(uri)

            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }


    suspend fun importNumberList(
        context: Context,
        file: Uri
    ) {
        withContext(Dispatchers.IO) {
            try {
                val db = DBRepository(context)
                val gson = Gson()

                context.contentResolver.openInputStream(file)?.use { uri ->
                    InputStreamReader(uri).use {
                        reader ->
                        val lists = gson.fromJson(reader, DataContainer::class.java)

                        if (lists == null) {
                            Log.e("Import", "Not completed parsing ")
                        } else {
                            val black = db.getNumbersSync(DataKeys.blackListKey)
                            val white = db.getNumbersSync(DataKeys.whitelistKey)

                            lists.blacklist.forEach {
                                if (!black.contains(it))
                                    db.add(it, DataKeys.blackListKey)
                            }
                            lists.whitelist.forEach {
                                if (!white.contains(it))
                                    db.add(it, DataKeys.whitelistKey)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Import", "Import error: $e")
            }
        }
    }
}