package com.bgflamer4ik.app.callblocker.service

import android.content.Intent
import android.telecom.Call
import android.telecom.CallScreeningService
import com.bgflamer4ik.app.callblocker.R
import com.bgflamer4ik.app.callblocker.database.DBRepository
import com.bgflamer4ik.app.callblocker.database.DataKeys
import com.bgflamer4ik.app.callblocker.service.CallBlockerMatchHelper.getPhoneNumberHandle
import com.bgflamer4ik.app.callblocker.service.CallBlockerMatchHelper.shouldBlockNumber

class CallBlockerService : CallScreeningService() {
    val dbRepo = DBRepository(this)

    override fun onScreenCall(p0: Call.Details) {
        var response: CallResponse
        if (p0.callDirection == Call.Details.DIRECTION_INCOMING) {
            val number = getPhoneNumberHandle(this, p0.handle) ?: p0.handle.schemeSpecificPart
            val isBlocked = shouldBlockNumber(this, number)

            response = CallResponse.Builder()
                .setDisallowCall(isBlocked)
                .setRejectCall(isBlocked)
                .setSkipCallLog(dbRepo.getKeySync(DataKeys.dataSkipCallLog) == "true")
                .setSkipNotification(dbRepo.getKeySync(DataKeys.dataSkipNotification) == "true")
                .build()

            notify(number, isBlocked)
        } else {
            response = CallResponse.Builder().build()
        }
        respondToCall(p0, response)
    }

    private fun notify(number: String, isBlocked: Boolean) {
        val text = "$number " +
                if (isBlocked) this.getString(R.string.notification_is_blocked)
                else this.getString(R.string.notification_passed)

        val intent = Intent(this, NotificationService::class.java)
            .putExtra("text", text)
            .putExtra("key", NotificationKeys.BLOCK_KEY)
        startForegroundService(intent)
    }
}