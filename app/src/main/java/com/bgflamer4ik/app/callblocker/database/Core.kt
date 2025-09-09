package com.bgflamer4ik.app.callblocker.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBCore(context: Context) : SQLiteOpenHelper(context, NAME, null, VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS blacklist(id INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT UNIQUE, hasPattern INTEGER)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS whitelist(id INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT UNIQUE, hasPattern INTEGER)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY AUTOINCREMENT, number TEXT, block INTEGER, params INTEGER)")
        db?.execSQL("CREATE TABLE IF NOT EXISTS keys(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, data TEXT)")
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        p1: Int,
        p2: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS blacklist")
        db?.execSQL("DROP TABLE IF EXISTS whitelist")
        db?.execSQL("DROP TABLE IF EXISTS history")
        db?.execSQL("DROP TABLE IF EXISTS keys")
        onCreate(db)
    }

    companion object {
        private const val VERSION = 1
        private const val NAME = "NumbersListDatabase.db"
    }

    interface DatabaseChangeListener{
        fun onDatabaseChanged()
    }

    object DatabaseChangeRegistry {
        private val listeners = mutableSetOf<DatabaseChangeListener>()

        fun register(listener: DatabaseChangeListener) {
            listeners.add(listener)
        }

        fun unregister(listener: DatabaseChangeListener) {
            listeners.remove(listener)
        }

        fun notifyDBChanges() {
            listeners.forEach { it.onDatabaseChanged() }
        }
    }
}