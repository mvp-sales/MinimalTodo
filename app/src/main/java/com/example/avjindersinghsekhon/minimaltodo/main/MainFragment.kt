package com.example.avjindersinghsekhon.minimaltodo.main

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.about.AboutActivity
import com.example.avjindersinghsekhon.minimaltodo.addToDo.AddToDoActivity
import com.example.avjindersinghsekhon.minimaltodo.addToDo.AddToDoFragment
import com.example.avjindersinghsekhon.minimaltodo.analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultFragment
import com.example.avjindersinghsekhon.minimaltodo.databinding.FragmentMainBinding
import com.example.avjindersinghsekhon.minimaltodo.databinding.ListCircleTryBinding
import com.example.avjindersinghsekhon.minimaltodo.reminder.ReminderFragment
import com.example.avjindersinghsekhon.minimaltodo.settings.SettingsActivity
import com.example.avjindersinghsekhon.minimaltodo.utility.ItemTouchHelperClass
import com.example.avjindersinghsekhon.minimaltodo.utility.ItemTouchHelperClass.ItemTouchHelperAdapter
import com.example.avjindersinghsekhon.minimaltodo.utility.StoreRetrieveData
import com.example.avjindersinghsekhon.minimaltodo.utility.ToDoItem
import com.example.avjindersinghsekhon.minimaltodo.utility.TodoNotificationService
import com.google.android.material.snackbar.Snackbar
import jahirfiquitiva.libs.textdrawable.TextDrawable
import org.json.JSONException
import java.io.IOException
import java.util.Collections
import java.util.Date

class MainFragment : AppDefaultFragment() {
    private lateinit var toDoItemsArrayList: ArrayList<ToDoItem>
    private lateinit var adapter: BasicListAdapter
    private lateinit var storeRetrieveData: StoreRetrieveData
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var customRecyclerScrollViewListener: CustomRecyclerScrollViewListener
    private var theme: String? = "name_of_the_theme"
    private lateinit var app: AnalyticsApplication
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = requireActivity().application as AnalyticsApplication
        binding = FragmentMainBinding.bind(view)

        //We recover the theme we've set and setTheme accordingly
        theme = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)
        val mTheme = if (theme == LIGHTTHEME) {
            R.style.CustomStyle_LightTheme
        } else {
            R.style.CustomStyle_DarkTheme
        }
        this.requireActivity().setTheme(mTheme)
        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(CHANGE_OCCURED, false)
        editor.apply()
        storeRetrieveData = StoreRetrieveData(requireContext(), FILENAME)
        toDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
        adapter = BasicListAdapter(toDoItemsArrayList)
        setAlarms()
        with(binding) {
            addToDoItemFAB.setOnClickListener {
                app.send("this", "Action", "FAB pressed")
                val newTodo = Intent(context, AddToDoActivity::class.java)
                val item = ToDoItem("", "", false, null)
                item.todoColor = Color.BLACK
                newTodo.putExtra(TODOITEM, item)
                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM)
            }
            if (theme == LIGHTTHEME) {
                toDoRecyclerView.setBackgroundColor(resources.getColor(R.color.primary_lightest))
            }
            toDoRecyclerView.setEmptyView(view.findViewById(R.id.toDoEmptyView))
            toDoRecyclerView.setHasFixedSize(true)
            toDoRecyclerView.itemAnimator = DefaultItemAnimator()
            toDoRecyclerView.layoutManager = LinearLayoutManager(context)
            customRecyclerScrollViewListener = object : CustomRecyclerScrollViewListener() {
                override fun show() {
                    addToDoItemFAB.animate().translationY(0f).setInterpolator(DecelerateInterpolator(2f)).start()
                }

                override fun hide() {
                    val lp = addToDoItemFAB.layoutParams as CoordinatorLayout.LayoutParams
                    val fabMargin = lp.bottomMargin
                    addToDoItemFAB.animate().translationY((addToDoItemFAB.height + fabMargin).toFloat()).setInterpolator(AccelerateInterpolator(2.0f)).start()
                }
            }
            toDoRecyclerView.addOnScrollListener(customRecyclerScrollViewListener)
            val callback: ItemTouchHelper.Callback = ItemTouchHelperClass(adapter)
            itemTouchHelper = ItemTouchHelper(callback)
            itemTouchHelper.attachToRecyclerView(toDoRecyclerView)
            toDoRecyclerView.setAdapter(adapter)
        }
    }

    override fun onResume() {
        super.onResume()
        app.send("this")
        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(ReminderFragment.EXIT, false)
            editor.apply()
            requireActivity().finish()
        }
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */
        if (requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)) {
            val editor = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE).edit()
            editor.putBoolean(RECREATE_ACTIVITY, false)
            editor.apply()
            requireActivity().recreate()
        }
    }

    override fun onStart() {
        app = requireActivity().application as AnalyticsApplication
        super.onStart()
        val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {
            toDoItemsArrayList = getLocallyStoredData(storeRetrieveData)
            adapter = BasicListAdapter(toDoItemsArrayList)
            binding.toDoRecyclerView.adapter = adapter
            setAlarms()
            val editor = sharedPreferences.edit()
            editor.putBoolean(CHANGE_OCCURED, false)
            editor.apply()
        }
    }

    private fun setAlarms() {
        if (toDoItemsArrayList.isNotEmpty()) {

            for (item in toDoItemsArrayList) {
                if (item.hasReminder && item.toDoDate != null) {
                    if (item.toDoDate!!.before(Date())) {
                        item.toDoDate = null
                        continue
                    }
                    val i = Intent(context, TodoNotificationService::class.java)
                    i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                    i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                    createAlarm(i, item.identifier.hashCode(), item.toDoDate!!.time)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutMeMenuItem -> {
                val i = Intent(context, AboutActivity::class.java)
                startActivity(i)
                true
            }

            R.id.preferences -> {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {
            val item = data?.getSerializableExtra(TODOITEM) as? ToDoItem
            item?.let {
                if (it.toDoText.isEmpty()) {
                    return
                }
            } ?: run {
                return
            }

            var existed = false
            if (item.hasReminder && item.toDoDate != null) {
                val i = Intent(context, TodoNotificationService::class.java)
                i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                createAlarm(i, item.identifier.hashCode(), item.toDoDate!!.time)
            }
            toDoItemsArrayList.indices
                .first { item.identifier == toDoItemsArrayList[it].identifier }
                .runCatching {
                    toDoItemsArrayList[this] = item
                    existed = true
                    adapter.notifyDataSetChanged()
                }
            if (!existed) {
                addToDataStore(item)
            }
        }
    }

    private fun doesPendingIntentExist(i: Intent, requestCode: Int): Boolean {
        val pi = PendingIntent.getService(requireContext(), requestCode, i, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        return pi != null
    }

    private fun createAlarm(i: Intent, requestCode: Int, timeInMillis: Long) {
        val am = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT)
        am[AlarmManager.RTC_WAKEUP, timeInMillis] = pi
    }

    private fun deleteAlarm(i: Intent, requestCode: Int) {
        if (doesPendingIntentExist(i, requestCode)) {
            val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
            pi.cancel()
            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pi)
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode))
        }
    }

    private fun addToDataStore(item: ToDoItem) {
        toDoItemsArrayList.add(item)
        adapter.notifyItemInserted(toDoItemsArrayList.size - 1)
    }

    inner class BasicListAdapter internal constructor(
        private val items: ArrayList<ToDoItem>
    ) : RecyclerView.Adapter<BasicListAdapter.ViewHolder?>(), ItemTouchHelperAdapter {
        override fun onItemMoved(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(items, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(items, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemRemoved(position: Int) {
            //Remove this line if not using Google Analytics
            app.send("this", "Action", "Swiped Todo Away")
            val justDeletedToDoItem = items.removeAt(position)
            val indexOfDeletedToDoItem = position
            val i = Intent(context, TodoNotificationService::class.java)
            deleteAlarm(i, justDeletedToDoItem.identifier.hashCode())
            notifyItemRemoved(position)
            val toShow = "Todo"
            Snackbar.make(binding.myCoordinatorLayout, "Deleted $toShow", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        app.send("this", "Action", "UNDO Pressed")
                        items.add(indexOfDeletedToDoItem, justDeletedToDoItem)
                        if (justDeletedToDoItem.toDoDate != null && justDeletedToDoItem.hasReminder) {
                            val intent = Intent(context, TodoNotificationService::class.java)
                            intent.putExtra(TodoNotificationService.TODOTEXT, justDeletedToDoItem.toDoText)
                            intent.putExtra(TodoNotificationService.TODOUUID, justDeletedToDoItem.identifier)
                            createAlarm(intent, justDeletedToDoItem.identifier.hashCode(), justDeletedToDoItem.toDoDate!!.time)
                        }
                        notifyItemInserted(indexOfDeletedToDoItem)
                    }
                    .show()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_circle_try, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            val sharedPreferences = requireActivity().getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
            //Background color for each to-do item. Necessary for night/day mode
            val bgColor: Int
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            val todoTextColor: Int
            if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME) == LIGHTTHEME) {
                bgColor = Color.WHITE
                todoTextColor = resources.getColor(R.color.secondary_text, context?.theme)
            } else {
                bgColor = Color.DKGRAY
                todoTextColor = Color.WHITE
            }
            with(holder.binding) {
                listItemLinearLayout.setBackgroundColor(bgColor)
                if (item.hasReminder && item.toDoDate != null) {
                    toDoListItemTextview.maxLines = 1
                    todoListItemTimeTextView.visibility = View.VISIBLE
                } else {
                    todoListItemTimeTextView.visibility = View.GONE
                    toDoListItemTextview.maxLines = 2
                }
                toDoListItemTextview.text = item.toDoText
                toDoListItemTextview.setTextColor(todoTextColor)
                val myDrawable = TextDrawable.builder()
                        .textColor(Color.WHITE)
                        .useFont(Typeface.DEFAULT)
                        .toUpperCase()
                        .buildRound(item.toDoText.substring(0, 1), item.todoColor)
                toDoListItemColorImageView.setImageDrawable(myDrawable)
                if (item.toDoDate != null) {
                    val timeToShow = if (DateFormat.is24HourFormat(context)) {
                        AddToDoFragment.formatDate(DATE_TIME_FORMAT_24_HOUR, item.toDoDate)
                    } else {
                        AddToDoFragment.formatDate(DATE_TIME_FORMAT_12_HOUR, item.toDoDate)
                    }
                    todoListItemTimeTextView.text = timeToShow
                }
            }

        }

        override fun getItemCount(): Int {
            return items.size
        }

        @Suppress("deprecation")
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val binding: ListCircleTryBinding

            init {
                binding = ListCircleTryBinding.bind(view)
                binding.root.setOnClickListener {
                    val item = items[this@ViewHolder.adapterPosition]
                    val i = Intent(context, AddToDoActivity::class.java)
                    i.putExtra(TODOITEM, item)
                    startActivityForResult(i, REQUEST_ID_TODO_ITEM)
                }
            }
        }
    }
    override fun onPause() {
        super.onPause()
        try {
            storeRetrieveData.saveToFile(toDoItemsArrayList)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.toDoRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener)
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_main
    }

    companion object {
        const val TODOITEM = "com.avjindersinghsekhon.minimaltodo.MainActivity"
        private const val REQUEST_ID_TODO_ITEM = 100
        const val DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a"
        const val DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm"
        const val FILENAME = "todoitems.json"
        const val SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged"
        const val CHANGE_OCCURED = "com.avjinder.changeoccured"
        const val THEME_PREFERENCES = "com.avjindersekhon.themepref"
        const val RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity"
        const val THEME_SAVED = "com.avjindersekhon.savedtheme"
        const val DARKTHEME = "com.avjindersekon.darktheme"
        const val LIGHTTHEME = "com.avjindersekon.lighttheme"
        fun getLocallyStoredData(storeRetrieveData: StoreRetrieveData): ArrayList<ToDoItem> {
            try {
                return storeRetrieveData.loadFromFile()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return arrayListOf()
        }

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}
