package com.example.avjindersinghsekhon.minimaltodo.reminder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultFragment
import com.example.avjindersinghsekhon.minimaltodo.databinding.FragmentReminderBinding
import com.example.avjindersinghsekhon.minimaltodo.main.MainActivity
import com.example.avjindersinghsekhon.minimaltodo.main.MainFragment
import com.example.avjindersinghsekhon.minimaltodo.utility.TodoNotificationReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class ReminderFragment : AppDefaultFragment(), MenuProvider {
    private lateinit var snoozeOptionsArray: Array<String>
    private var theme: String? = null
    private lateinit var todoId: UUID
    private lateinit var app: AnalyticsApplication
    private lateinit var binding: FragmentReminderBinding
    private val viewModel: ReminderViewModel by activityViewModels()

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

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        (activity as? AppCompatActivity)?.setSupportActionBar(view.findViewById<View>(R.id.toolbar) as Toolbar)
        val i = requireActivity().intent
        todoId = i.getSerializableExtra(TodoNotificationReceiver.TODOUUID) as UUID
        snoozeOptionsArray = resources.getStringArray(R.array.snooze_options)

        with(binding) {
            if (theme == MainFragment.LIGHTTHEME) {
                reminderViewSnoozeTextView.setTextColor(resources.getColor(R.color.secondary_text))
            } else {
                reminderViewSnoozeTextView.setTextColor(Color.WHITE)
                reminderViewSnoozeTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_snooze_white_24dp, 0, 0, 0
                )
            }
            toDoReminderRemoveButton.setOnClickListener { view ->
                onReminderDone(view)
            }
            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_text_view, snoozeOptionsArray)
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            todoReminderSnoozeSpinner.adapter = adapter
        }
        lifecycleScope.launch {
            viewModel.getTodo(todoId).collect {
                binding.toDoReminderTextViewBody.text = it.title
            }
        }
    }

    private fun onReminderDone(view: View) {
        app.send("this", "Action", "Todo Removed from Reminder Activity")
        lifecycleScope.launch {
            viewModel.deleteTodo(todoId).collect {
                changeOccurred()
                closeApp()
            }
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_reminder
    }

    private fun closeApp() {
        val i = Intent(context, MainActivity::class.java)
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val sharedPreferences = requireActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(EXIT, true)
        }
        startActivity(i)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_reminder, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
        when (menuItem.itemId) {
            R.id.toDoReminderDoneMenuItem -> {
                val date = addTimeToDate(valueFromSpinner())
                lifecycleScope.launch {
                    viewModel.updateTodo(todoId, date).collect {
                        Log.d("OskarSchindler", "Date Changed to: $date")
                        changeOccurred()
                        closeApp()
                    }
                }
                true
            }
            else -> false
        }

    private fun changeOccurred() {
        val sharedPreferences = requireActivity().getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(MainFragment.CHANGE_OCCURED, true)
        }
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

    companion object {
        const val EXIT = "com.avjindersekhon.exit"
        fun newInstance(): ReminderFragment {
            return ReminderFragment()
        }
    }
}
