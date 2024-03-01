package com.example.avjindersinghsekhon.minimaltodo.utility

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.Date
import java.util.UUID

class ToDoItem(
    var toDoText: String = "Clean my room",
    var toDoDescription: String = "Sweep and Mop my Room",
    var hasReminder: Boolean = true,
    var toDoDate: Date? = Date(),
    var todoColor: Int = 1677725,
    val identifier: UUID = UUID.randomUUID()
): Serializable {
    //    private Date mLastEdited;

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(TODOTEXT, toDoText)
        jsonObject.put(TODOREMINDER, hasReminder)
        jsonObject.put(TODODESCRIPTION, toDoDescription)
        //        jsonObject.put(TODOLASTEDITED, mLastEdited.getTime());
        toDoDate?.let {
            jsonObject.put(TODODATE, it.time)
        }
        jsonObject.put(TODOCOLOR, todoColor)
        jsonObject.put(TODOIDENTIFIER, identifier.toString())
        return jsonObject
    }

    companion object {
        //add description
        private const val TODODESCRIPTION = "tododescription"
        private const val TODOTEXT = "todotext"
        private const val TODOREMINDER = "todoreminder"

        //    private static final String TODOLASTEDITED = "todolastedited";
        private const val TODOCOLOR = "todocolor"
        private const val TODODATE = "tododate"
        private const val TODOIDENTIFIER = "todoidentifier"

        @Throws(JSONException::class)
        fun fromJSON(jsonObject: JSONObject): ToDoItem {
            val toDoText = jsonObject.getString(TODOTEXT)
            val toDoDescription = jsonObject.getString(TODODESCRIPTION)
            val hasReminder = jsonObject.getBoolean(TODOREMINDER)
            val todoColor = jsonObject.getInt(TODOCOLOR)
            val identifier = UUID.fromString(jsonObject.getString(TODOIDENTIFIER))
            val toDoDate = if (jsonObject.has(TODODATE)) {
                Date(jsonObject.getLong(TODODATE))
            } else {
                null
            }
//        if(jsonObject.has(TODOLASTEDITED)){
//            mLastEdited = new Date(jsonObject.getLong(TODOLASTEDITED));
//        }
            return ToDoItem(
                toDoText,
                toDoDescription,
                hasReminder,
                toDoDate,
                todoColor,
                identifier
            )
        }
    }
}
