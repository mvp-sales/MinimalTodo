package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf

class DeleteNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getUUIDExtra(TodoNotificationReceiver.TODOUUID)?.let { todoId ->
            val workRequest = OneTimeWorkRequest.Builder(DeleteNotificationService::class.java)
                .setInputData(
                    workDataOf(
                        TodoNotificationReceiver.TODOUUID to todoId
                    )
                ).build()
            context?.let {
                WorkManager.getInstance(it).enqueue(workRequest)
            }
        }
    }
}