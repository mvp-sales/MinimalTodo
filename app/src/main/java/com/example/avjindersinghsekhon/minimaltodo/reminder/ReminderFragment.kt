package com.example.avjindersinghsekhon.minimaltodo.reminder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultFragment
import com.example.avjindersinghsekhon.minimaltodo.databinding.FragmentReminderBinding
import com.example.avjindersinghsekhon.minimaltodo.main.MainActivity
import com.example.avjindersinghsekhon.minimaltodo.main.MainFragment
import com.example.avjindersinghsekhon.minimaltodo.utility.StoreRetrieveData
import com.example.avjindersinghsekhon.minimaltodo.utility.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.utility.TodoNotificationService
import org.json.JSONException
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.UUID

class ReminderFragment : AppDefaultFragment() {
    private lateinit var snoozeOptionsArray: Array<String>
    private lateinit var storeRetrieveData: StoreRetrieveData
    private lateinit var toDoItems: ArrayList<ToDoItem>
    private var item: ToDoItem? = null
    private var theme: String? = null
    private lateinit var app: AnalyticsApplication
    private lateinit var binding: FragmentReminderBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reminder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReminderBinding.bind(view)
        app = requireActivity().application as AnalyticsApplication
        app.send("this")
        theme = requireActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)
        if (theme == MainFragment.LIGHTTHEME) {
            requireActivity().setTheme(R.style.CustomStyle_LightTheme)
        } else {
            requireActivity().setTheme(R.style.CustomStyle_DarkTheme)
        }
        storeRetrieveData = StoreRetrieveData(requireContext(), MainFragment.FILENAME)
        toDoItems = MainFragment.getLocallyStoredData(storeRetrieveData)
        (activity as? AppCompatActivity)?.setSupportActionBar(view.findViewById<View>(R.id.toolbar) as Toolbar)
        val i = requireActivity().intent
        val id = i.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID
        item = toDoItems.firstOrNull { it.identifier == id }
        snoozeOptionsArray = resources.getStringArray(R.array.snooze_options)
        with(binding) {
            toDoReminderTextViewBody.text = item?.toDoText
            if (theme == MainFragment.LIGHTTHEME) {
                reminderViewSnoozeTextView.setTextColor(resources.getColor(R.color.secondary_text))
            } else {
                reminderViewSnoozeTextView.setTextColor(Color.WHITE)
                reminderViewSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_snooze_white_24dp, 0, 0, 0
                )
            }
            toDoReminderRemoveButton.setOnClickListener {
                app.send("this", "Action", "Todo Removed from Reminder Activity")
                toDoItems.remove(item)
                changeOccurred()
                saveData()
                closeApp()
            }
            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_text_view, snoozeOptionsArray)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            todoReminderSnoozeSpinner.adapter = adapter
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_reminder
    }

    private fun closeApp() {
        val i = Intent(context, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val sharedPreferences = requireActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(EXIT, true)
        editor.apply()
        startActivity(i)
    }

    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        requireActivity().menuInflater.inflate(R.menu.menu_reminder, menu)
        return true
    }

    private fun changeOccurred() {
        val sharedPreferences = requireActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true)
        editor.apply()
    }

    private fun addTimeToDate(mins: Int): Date {
        app.send("this", "Action", "Snoozed", "For $mins minutes")
        val date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, mins)
        return calendar.time
    }

    private fun valueFromSpinner(): Int {
        return when (binding.todoReminderSnoozeSpinner.selectedItemPosition) {
            0 -> 10
            1 -> 30
            2 -> 60
            else -> 0
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toDoReminderDoneMenuItem -> {
                val date = addTimeToDate(valueFromSpinner())
                this.item?.toDoDate = date
                this.item?.hasReminder = true
                Log.d("OskarSchindler", "Date Changed to: $date")
                changeOccurred()
                saveData()
                closeApp()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveData() {
        try {
            storeRetrieveData.saveToFile(toDoItems)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val EXIT = "com.avjindersekhon.exit"
        fun newInstance(): ReminderFragment {
            return ReminderFragment()
        }
    }
}
