package com.bgflamer4ik.app.callblocker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bgflamer4ik.app.callblocker.database.DBRepository
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.database.HistoryData
import com.bgflamer4ik.app.callblocker.database.KeyData
import com.bgflamer4ik.app.callblocker.database.NumberData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ApplicationViewModel(application: Application): AndroidViewModel(application) {
    private val db = DBRepository(application)
    private val _blacklist = db.getNumbers("blacklist")
    private val _whitelist = db.getNumbers("whitelist")
    private val _history = db.getHistory()

    val blacklist = _blacklist.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val whitelist = _whitelist.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val history = _history.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _blockUndefined = db.getKey(DataKeys.dataBlockUndefined)
    private val _blockAll = db.getKey(DataKeys.dataBlockAll)
    private val _skipCallLog = db.getKey(DataKeys.dataSkipCallLog)
    private val _skipNotification = db.getKey(DataKeys.dataSkipNotification)

    val blockUndefined = _blockUndefined.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5000), "false")
    val blockAll = _blockAll.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "false")
    val skipCallLog = _skipCallLog.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "false")
    val skipNotification = _skipNotification.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "false")

    fun add(number: NumberData, listName: String) {
        db.add(number, listName)
    }

    fun update(old: NumberData, new: NumberData, listName: String) {
        db.update(old, new, listName)
    }

    fun update(key: String, value: Boolean) {
        val data = KeyData(
            name = key,
            data = if (value) "true" else "false"
        )
        db.update(data)
    }

    fun remove(number: NumberData, listName: String) {
        db.remove(number, listName)
    }

    fun remove(number: HistoryData) {
        db.remove(number)
    }
}