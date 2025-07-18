package com.example.avjindersinghsekhon.minimaltodo.addToDo

import android.animation.Animator
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultFragment
import com.example.avjindersinghsekhon.minimaltodo.database.Todo
import com.example.avjindersinghsekhon.minimaltodo.databinding.FragmentAddToDoBinding
import com.example.avjindersinghsekhon.minimaltodo.main.MainFragment
import com.example.avjindersinghsekhon.minimaltodo.utility.TodoNotificationReceiver
import com.example.avjindersinghsekhon.minimaltodo.utility.getUUIDExtra
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class AddToDoFragment : AppDefaultFragment(), MenuProvider, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private var theme: String? = null
    private lateinit var app: AnalyticsApplication
    private lateinit var binding: FragmentAddToDoBinding
    private val viewModel: AddTodoViewModel by activityViewModels()
    private var reminderDate: Date? = null
    private var todo: Todo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_to_do, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddToDoBinding.bind(view)
        app = requireActivity().application as AnalyticsApplication

        theme = requireActivity().getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)
        if (theme == MainFragment.LIGHTTHEME) {
            requireActivity().setTheme(R.style.CustomStyle_LightTheme)
            Log.d("OskarSchindler", "Light Theme")
        } else {
            requireActivity().setTheme(R.style.CustomStyle_DarkTheme)
        }

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        //Show an X in place of <-
        val cross = ResourcesCompat.getDrawable(resources, R.drawable.ic_clear_white_24dp, requireContext().theme)
        cross?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(resources.getColor(R.color.icons, requireContext().theme), BlendModeCompat.SRC_ATOP)
        val toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (requireActivity() as? AppCompatActivity)?.let {
            it.setSupportActionBar(toolbar)
            it.supportActionBar?.let { actionBar ->
                actionBar.elevation = 0f
                actionBar.setDisplayShowTitleEnabled(false)
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(cross)
            }
        }

        (requireActivity().intent.getUUIDExtra(AddToDoActivity.TODO_ID))?.let { todoId ->
            lifecycleScope.launch {
                viewModel.getTodo(todoId).collect {
                    this@AddToDoFragment.todo = it
                    if (it.hasReminder && it.date != null) {
                        setReminderTextView(it.date)
                        setEnterDateLayoutVisibleWithAnimations(true)
                    }
                    if (it.date == null) {
                        binding.newToDoDateTimeReminderTextView.visibility = View.INVISIBLE
                    }
                    binding.toDoHasDateSwitchCompat.isChecked = it.hasReminder && it.date != null
                    setEnterDateLayoutVisible(binding.toDoHasDateSwitchCompat.isChecked)
                    binding.userToDoEditText.requestFocus()
                    binding.userToDoEditText.setText(it.title)
                    binding.userToDoDescription.setText(it.description)
                    setDateAndTimeEditText(it)
                }
            }
        }

        //Button for Copy to Clipboard=
        with(binding) {
            if (theme == MainFragment.DARKTHEME) {
                val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_alarm_add_white_24dp, requireContext().theme)
                userToDoReminderIconImageButton.setImageDrawable(drawable)
                newToDoDateTimeReminderTextView.setTextColor(Color.WHITE)
            }
            copyclipboard.setOnClickListener {
                val toDoTextContainer = userToDoEditText.text.toString()
                val toDoTextBodyDescriptionContainer = userToDoDescription.text.toString()
                val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val combinationText = "Title : $toDoTextContainer\nDescription : $toDoTextBodyDescriptionContainer\n -Copied From MinimalToDo"
                val clip = ClipData.newPlainText("text", combinationText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Copied To Clipboard!", Toast.LENGTH_SHORT).show()
            }

            todoReminderAndDateContainerLayout.setOnClickListener {
                hideKeyboard(userToDoEditText)
                hideKeyboard(userToDoDescription)
            }

            toDoHasDateSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    app.send("this", "Action", "Reminder Set")
                } else {
                    app.send("this", "Action", "Reminder Removed")
                }
                //setDateAndTimeEditText()
                setEnterDateLayoutVisibleWithAnimations(isChecked)
                hideKeyboard(userToDoEditText)
                hideKeyboard(userToDoDescription)
            }
            makeToDoFloatingActionButton.setOnClickListener {
                if (userToDoEditText.length() <= 0) {
                    userToDoEditText.error = getString(R.string.todo_error)
                } else if (reminderDate != null && reminderDate!!.before(Date())) {
                    app.send("this", "Action", "Date in the Past")
                    Toast.makeText(requireContext(), "Reminder date in the past!", Toast.LENGTH_LONG).show()
                } else {
                    app.send("this", "Action", "Make Todo")
                    val todo = this@AddToDoFragment.todo ?: Todo()
                    todo.title = userToDoEditText.text.toString()
                    todo.description = userToDoDescription.text.toString()
                    todo.hasReminder = toDoHasDateSwitchCompat.isChecked
                    todo.date = reminderDate
                    lifecycleScope.launch {
                        viewModel.addTodo(todo).collect {
                            createAlarm(todo)
                            requireActivity().finish()
                        }
                    }
                }
                hideKeyboard(userToDoEditText)
                hideKeyboard(userToDoDescription)
            }
            newTodoDateEditText.setOnClickListener {
                hideKeyboard(userToDoEditText)
                val date = reminderDate ?: Date()
                val calendar = Calendar.getInstance()
                calendar.time = date
                val year = calendar[Calendar.YEAR]
                val month = calendar[Calendar.MONTH]
                val day = calendar[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    if (theme == MainFragment.DARKTHEME) R.style.CustomStyle_DarkTheme else R.style.CustomStyle_LightTheme,
                    this@AddToDoFragment,
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }
            newTodoTimeEditText.setOnClickListener {
                hideKeyboard(userToDoEditText)
                val date = reminderDate ?: Date()
                val calendar = Calendar.getInstance()
                calendar.time = date
                val hour = calendar[Calendar.HOUR_OF_DAY]
                val minute = calendar[Calendar.MINUTE]
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    if (theme == MainFragment.DARKTHEME) R.style.CustomStyle_DarkTheme else R.style.CustomStyle_LightTheme,
                    this@AddToDoFragment,
                    hour,
                    minute,
                    DateFormat.is24HourFormat(requireContext())
                )
                timePickerDialog.show()
            }
        }
    }

    private fun setDateAndTimeEditText(todo: Todo) {
        if (todo.hasReminder && todo.date != null) {
            val userDate = formatDate("d MMM, yyyy", todo.date)
            val formatToUse = if (DateFormat.is24HourFormat(context)) {
                "k:mm"
            } else {
                "h:mm a"
            }
            val userTime = formatDate(formatToUse, todo.date)
            binding.newTodoTimeEditText.setText(userTime)
            binding.newTodoDateEditText.setText(userDate)
        } else {
            binding.newTodoDateEditText.setText(getString(R.string.date_reminder_default))
            val time24 = DateFormat.is24HourFormat(context)
            val cal = Calendar.getInstance()
            if (time24) {
                cal[Calendar.HOUR_OF_DAY] = cal[Calendar.HOUR_OF_DAY] + 1
            } else {
                cal[Calendar.HOUR] = cal[Calendar.HOUR] + 1
            }
            cal[Calendar.MINUTE] = 0
            reminderDate = todo.date
            Log.d("OskarSchindler", "Imagined Date: ${todo.date}")
            val timeString = if (time24) {
                formatDate("k:mm", todo.date)
            } else {
                formatDate("h:mm a", todo.date)
            }
            binding.newTodoTimeEditText.setText(timeString)
        }
    }

    private fun hideKeyboard(et: EditText?) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et?.windowToken, 0)
    }

    private fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val reminderCalendar = Calendar.getInstance()
        reminderCalendar[year, month] = day
        if (reminderCalendar.before(calendar)) {
            //    Toast.makeText(this, "My time-machine is a bit rusty", Toast.LENGTH_SHORT).show();
            return
        }
        if (reminderDate != null) {
            calendar.time = reminderDate!!
        }
        val hour = if (DateFormat.is24HourFormat(context)) {
            calendar[Calendar.HOUR_OF_DAY]
        } else {
            calendar[Calendar.HOUR]
        }
        val minute = calendar[Calendar.MINUTE]
        calendar[year, month, day, hour] = minute
        reminderDate = calendar.time
        setReminderTextView(reminderDate)
        setDateEditText(reminderDate!!)
    }

    private fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        if (reminderDate != null) {
            calendar.time = reminderDate!!
        }
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        Log.d("OskarSchindler", "Time set: $hour")
        calendar[year, month, day, hour, minute] = 0
        reminderDate = calendar.time
        setReminderTextView(reminderDate)
        setTimeEditText(reminderDate!!)
    }

    private fun setDateEditText(date: Date) {
        val dateFormat = "d MMM, yyyy"
        binding.newTodoDateEditText.setText(formatDate(dateFormat, date))
    }

    private fun setTimeEditText(date: Date) {
        val dateFormat = if (DateFormat.is24HourFormat(context)) {
            "k:mm"
        } else {
            "h:mm a"
        }
        binding.newTodoTimeEditText.setText(formatDate(dateFormat, date))
    }

    private fun setReminderTextView(todoDate: Date?) {
        todoDate?.let { userReminderDate ->
            with(binding) {
                newToDoDateTimeReminderTextView.visibility = View.VISIBLE
                if (userReminderDate.before(Date())) {
                    Log.d("OskarSchindler", "DATE is $userReminderDate")
                    newToDoDateTimeReminderTextView.text = getString(R.string.date_error_check_again)
                    newToDoDateTimeReminderTextView.setTextColor(Color.RED)
                    return
                }
                val dateString = formatDate("d MMM, yyyy", userReminderDate)
                val timeString: String
                var amPmString = ""
                if (DateFormat.is24HourFormat(context)) {
                    timeString = formatDate("k:mm", userReminderDate)
                } else {
                    timeString = formatDate("h:mm", userReminderDate)
                    amPmString = formatDate("a", userReminderDate)
                }
                val finalString = String.format(resources.getString(R.string.remind_date_and_time), dateString, timeString, amPmString)
                newToDoDateTimeReminderTextView.setTextColor(resources.getColor(R.color.secondary_text, requireContext().theme))
                newToDoDateTimeReminderTextView.text = finalString
            }
        } ?: run {
            binding.newToDoDateTimeReminderTextView.visibility = View.INVISIBLE
        }
    }

    private fun createAlarm(todo: Todo) {
        val intent = Intent(requireContext(), TodoNotificationReceiver::class.java).apply {
            putExtra(TodoNotificationReceiver.TODOTEXT, todo.title)
            putExtra(TodoNotificationReceiver.TODOUUID, todo.identifier)
        }
        val am = requireActivity().getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pi = PendingIntent.getService(
            requireContext(),
            todo.identifier.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        todo.date?.let { date ->
            if (am != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
                    am.setExact(AlarmManager.RTC_WAKEUP, date.time, pi)
                } else {
                    am.setExact(AlarmManager.RTC_WAKEUP, date.time, pi)
                    //am.set(AlarmManager.RTC_WAKEUP, date.time, pi)
                }
            }

        }
        /*am?.let { mgr ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && mgr.canScheduleExactAlarms()) {
                mgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, todo.date!!.time, pi)
            } else {
                //
                mgr.setExact(AlarmManager.RTC_WAKEUP, todo.date!!.time, pi)
                //mgr.set(AlarmManager.RTC_WAKEUP, todo.date!!.time, pi)
            }
        }*/
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(requireActivity()) != null) {
                    app.send("this", "Action", "Discard Todo")
                    NavUtils.navigateUpFromSameTask(requireActivity())
                }
                hideKeyboard(binding.userToDoEditText)
                true
            }
            else -> false
        }
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        setDate(year, month, dayOfMonth)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        setTime(hourOfDay, minute)
    }
    private fun setEnterDateLayoutVisible(checked: Boolean) {
        binding.toDoEnterDateLinearLayout.visibility = if (checked) View.VISIBLE else View.INVISIBLE
    }

    private fun setEnterDateLayoutVisibleWithAnimations(checked: Boolean) {
        if (checked) {
            setReminderTextView(null)
            binding.toDoEnterDateLinearLayout.animate().alpha(1.0f).setDuration(500).setListener(
                    object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            binding.toDoEnterDateLinearLayout.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator) {}
                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    }
            )
        } else {
            binding.toDoEnterDateLinearLayout.animate().alpha(0.0f).setDuration(500).setListener(
                    object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {}
                        override fun onAnimationEnd(animation: Animator) {
                            binding.toDoEnterDateLinearLayout.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    }
            )
        }
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_add_to_do
    }

    companion object {
        private const val TAG = "AddToDoFragment"
        fun formatDate(formatString: String?, dateToFormat: Date?): String {
            val simpleDateFormat = SimpleDateFormat(formatString!!)
            return simpleDateFormat.format(dateToFormat!!)
        }

        fun newInstance(): AddToDoFragment {
            return AddToDoFragment()
        }
    }
}
