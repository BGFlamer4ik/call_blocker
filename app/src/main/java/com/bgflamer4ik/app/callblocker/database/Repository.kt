package com.bgflamer4ik.app.callblocker.database

import android.content.ContentValues
import android.content.Context
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DBRepository(context: Context) {
    private val dbHelp = DBCore(context)

    /////////////////
    ///For ViewModels
    /////////////////

    fun getNumbers(listName: String?): Flow<List<NumberData>> = callbackFlow {
        val listener = object: DBCore.DatabaseChangeListener {
            override fun onDatabaseChanged() {
                trySend(getNumbersSync(listName))
            }
        }
        DBCore.DatabaseChangeRegistry.register(listener)
        trySend(getNumbersSync(listName))
        awaitClose { DBCore.DatabaseChangeRegistry.unregister(listener) }
    }

    fun getHistory(): Flow<List<HistoryData>> = callbackFlow {
        val listener = object: DBCore.DatabaseChangeListener {
            override fun onDatabaseChanged() {
                trySend(getHistorySync())
            }
        }
        DBCore.DatabaseChangeRegistry.register(listener)
        trySend(getHistorySync())
        awaitClose { DBCore.DatabaseChangeRegistry.unregister(listener) }
    }

    fun getKey(name: String): Flow<String?> = callbackFlow {
        val listener = object: DBCore.DatabaseChangeListener {
            override fun onDatabaseChanged() {
                trySend(getKeySync(name))
            }
        }
        DBCore.DatabaseChangeRegistry.register(listener)
        trySend(getKeySync(name))
        awaitClose { DBCore.DatabaseChangeRegistry.unregister(listener) }
    }

    fun getKeySync(name: String): String? {
        val db = dbHelp.readableDatabase
        val cursor = db.query("keys", null, "name = ?", arrayOf(name), null, null, null)
        val data = if (cursor.moveToNext()) {
            cursor.getString(cursor.getColumnIndexOrThrow("data"))
        } else {
            null
        }
        cursor.close()
        db.close()
        return data
    }

    /////////////
    ///Sync funs
    /////////////

    fun getNumbersSync(listName: String?): List<NumberData> {
        val db = dbHelp.readableDatabase
        val cursor = db.query(listName ?: "blacklist", null, null, null, null, null, null)
        val list = mutableListOf<NumberData>()

        while (cursor.moveToNext()) {
            list.add(
                NumberData(
                    number = cursor.getString(cursor.getColumnIndexOrThrow("number")),
                    hasPattern = cursor.getInt(cursor.getColumnIndexOrThrow("hasPattern")) == 1
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }

    private fun getHistorySync(): List<HistoryData> {
        val db = dbHelp.readableDatabase
        val cursor = db.query("history", null, null, null, null, null, null)
        val list = mutableListOf<HistoryData>()

        while(cursor.moveToNext()) {
            list.add(
                HistoryData(
                    number = cursor.getString(cursor.getColumnIndexOrThrow("number")),
                    block = cursor.getInt(cursor.getColumnIndexOrThrow("block")) == 1,
                    pattern = cursor.getInt(cursor.getColumnIndexOrThrow("pattern"))
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }

    ////////////
    // Add funs
    ////////////

    fun add(entry: NumberData, tableName: String) {
        val db = dbHelp.writableDatabase
        val values = ContentValues().apply {
            put("number", entry.number)
            put("hasPattern", if (entry.hasPattern) 1 else 0)
        }
        db.insert(tableName, null, values)
        db.close()
        notifyDBChanges()
    }

    fun add(entry: HistoryData) {
        val db = dbHelp.writableDatabase
        val values = ContentValues().apply {
            put("number", entry.number)
            put("block",  if (entry.block) 1 else 0 )
            put("pattern", entry.pattern)
        }
        db.insert("history", null, values)
        db.close()
        notifyDBChanges()
    }

    fun add(entry: KeyData) {
        val db = dbHelp.writableDatabase
        val values = ContentValues().apply {
            put("name", entry.name)
            put("data", entry.data)
        }
        db.insert("keys", null, values)
        db.close()
        notifyDBChanges()
    }

    //////////////
    ///Update funs
    //////////////
    fun update(keyData: KeyData) {
        val db = dbHelp.writableDatabase
        val values = ContentValues().apply {
            put("name", keyData.name)
            put("data", keyData.data)
        }
        db.update("keys", values, "name = ?", arrayOf(keyData.name))
        db.close()
        notifyDBChanges()
    }

    fun update(old: NumberData, new: NumberData, tableName: String) {
        val db = dbHelp.writableDatabase
        val values = ContentValues().apply {
            put("number", new.number)
            put("hasPattern", if (new.hasPattern) 1 else 0)
        }
        db.update(tableName, values, "number = ?", arrayOf(old.number))
        db.close()
        notifyDBChanges()
    }

    ////////////////
    ///Remove funs
    ////////////////

    fun remove(entry: NumberData, tableName: String) {
        val db = dbHelp.writableDatabase
        db.delete(tableName, "number = ?", arrayOf(entry.number))
        db.close()
        notifyDBChanges()
    }

    fun remove(entry: HistoryData) {
        val db = dbHelp.writableDatabase
        db.delete("history", "number = ?", arrayOf(entry.number))
        db.close()
        notifyDBChanges()
    }

    ///////
    ////End
    private fun notifyDBChanges() {
        DBCore.DatabaseChangeRegistry.notifyDBChanges()
    }
}