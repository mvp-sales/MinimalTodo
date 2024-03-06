package com.example.avjindersinghsekhon.minimaltodo.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.addToDo.AddToDoActivity
import com.example.avjindersinghsekhon.minimaltodo.addToDo.AddToDoFragment
import com.example.avjindersinghsekhon.minimaltodo.database.Todo
import com.example.avjindersinghsekhon.minimaltodo.databinding.ListCircleTryBinding
import com.example.avjindersinghsekhon.minimaltodo.utility.ItemTouchHelperClass
import com.example.avjindersinghsekhon.minimaltodo.utility.TodoNotificationService
import com.google.android.material.snackbar.Snackbar
import jahirfiquitiva.libs.textdrawable.TextDrawable
import java.util.Collections

class TodoListAdapter(
    private val items: List<Todo>,
    private val context: Context,
    private val listener: TodoItemListener
): RecyclerView.Adapter<TodoListAdapter.ViewHolder>(), ItemTouchHelperClass.ItemTouchHelperAdapter {

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
        /*app.send("this", "Action", "Swiped Todo Away")
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
                .show()*/
        listener.removeTodo(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_circle_try, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        /*val sharedPreferences = context.getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE)
        //Background color for each to-do item. Necessary for night/day mode
        val bgColor: Int
        //color of title text in our to-do item. White for night mode, dark gray for day mode
        val todoTextColor: Int
        if (sharedPreferences.getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME) == MainFragment.LIGHTTHEME) {
            bgColor = Color.WHITE
            todoTextColor = context.resources.getColor(R.color.secondary_text, context.theme)
        } else {
            bgColor = Color.DKGRAY
            todoTextColor = Color.WHITE
        }*/
        with(holder.binding) {
            listItemLinearLayout.setBackgroundColor(Color.WHITE)
            if (item.hasReminder && item.date != null) {
                toDoListItemTextview.maxLines = 1
                todoListItemTimeTextView.visibility = View.VISIBLE
            } else {
                todoListItemTimeTextView.visibility = View.GONE
                toDoListItemTextview.maxLines = 2
            }
            toDoListItemTextview.text = item.title
            toDoListItemTextview.setTextColor(context.resources.getColor(R.color.secondary_text, context.theme))
            val myDrawable = TextDrawable.builder()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .buildRound(item.title!!.substring(0, 1), Color.BLACK)
            toDoListItemColorImageView.setImageDrawable(myDrawable)
            if (item.date != null) {
                val timeToShow = if (DateFormat.is24HourFormat(context)) {
                    AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_24_HOUR, item.date)
                } else {
                    AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_12_HOUR, item.date)
                }
                todoListItemTimeTextView.text = timeToShow
            }

            root.setOnClickListener {
                /*val item = items[this@ViewHolder.adapterPosition]
                val i = Intent(context, AddToDoActivity::class.java)
                i.putExtra(MainFragment.TODOITEM, item)
                startActivityForResult(i, MainFragment.REQUEST_ID_TODO_ITEM)*/
                listener.openTodo(items[holder.bindingAdapterPosition])
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ListCircleTryBinding

        init {
            binding = ListCircleTryBinding.bind(view)
        }
    }
}