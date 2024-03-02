package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.avjindersinghsekhon.minimaltodo.main.MainFragment
import java.util.UUID

class DeleteNotificationService(
        appContext: Context,
        workerParams: WorkerParameters
): Worker(appContext, workerParams) {
    private lateinit var storeRetrieveData: StoreRetrieveData
    private lateinit var toDoItems: ArrayList<ToDoItem>

    override fun doWork(): Result {
        storeRetrieveData = StoreRetrieveData(applicationContext, MainFragment.FILENAME)

        val todoID = inputData.getString(TodoNotificationService.TODOUUID)
        toDoItems = loadData()
        if (toDoItems.isNotEmpty()) {
            toDoItems.firstOrNull { it.identifier.toString() == todoID }?.let {
                toDoItems.remove(it)
                dataChanged()
                saveData()
            }
        }
        return Result.success()
    }

    private fun dataChanged() {
        val sharedPreferences = applicationContext.getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true)
        editor.apply()
    }

    private fun saveData() {
        try {
            storeRetrieveData.saveToFile(toDoItems)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadData(): ArrayList<ToDoItem> {
        try {
            return storeRetrieveData.loadFromFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayListOf()
    }
}
