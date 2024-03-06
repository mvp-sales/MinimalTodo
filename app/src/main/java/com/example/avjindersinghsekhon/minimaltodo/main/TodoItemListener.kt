package com.example.avjindersinghsekhon.minimaltodo.main

import com.example.avjindersinghsekhon.minimaltodo.database.Todo

interface TodoItemListener {
    fun openTodo(item: Todo)
    fun removeTodo(item: Todo)
}