package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.avjindersinghsekhon.minimaltodo.main.MainFragment
import com.example.avjindersinghsekhon.minimaltodo.repositories.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class DeleteNotificationService @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: TodoRepository
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        inputData.getString(TodoNotificationReceiver.TODOUUID)?.let {
            val todoId = UUID.fromString(it)
            repository.deleteById(todoId).collect {
                dataChanged()
            }
            return Result.success()
        }
        return Result.success()
    }

    private fun dataChanged() {
        val sharedPreferences = applicationContext.getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(MainFragment.CHANGE_OCCURED, true)
        }
    }
}
