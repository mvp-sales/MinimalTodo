package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class StoreRetrieveData(
        private val context: Context,
        private val fileName: String
) {
    @Throws(JSONException::class, IOException::class)
    fun saveToFile(items: ArrayList<ToDoItem>) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fileOutputStream ->
            OutputStreamWriter(fileOutputStream).use {
                it.write(toJSONArray(items).toString())
            }
        }
    }

    @Throws(IOException::class, JSONException::class)
    fun loadFromFile(): ArrayList<ToDoItem> {
        val items = ArrayList<ToDoItem>()
        try {
            context.openFileInput(fileName).use { fileInputStream ->
                val builder = StringBuilder()
                var line: String?
                BufferedReader(InputStreamReader(fileInputStream)).use { bufferedReader ->
                    while (bufferedReader.readLine().also { line = it } != null) {
                        builder.append(line)
                    }
                    val jsonArray = JSONTokener(builder.toString()).nextValue() as JSONArray
                    for (i in 0 until jsonArray.length()) {
                        val item = ToDoItem.fromJSON(jsonArray.getJSONObject(i))
                        items.add(item)
                    }
                }
            }
        } catch (fnfe: FileNotFoundException) {
            //do nothing about it
            //file won't exist first time app is run
        }
        return items
    }

    companion object {
        @Throws(JSONException::class)
        fun toJSONArray(items: ArrayList<ToDoItem>): JSONArray {
            val jsonArray = JSONArray()
            for (item in items) {
                val jsonObject = item.toJSON()
                jsonArray.put(jsonObject)
            }
            return jsonArray
        }
    }
}
