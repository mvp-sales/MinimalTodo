package com.example.avjindersinghsekhon.minimaltodo.repositories

import com.example.avjindersinghsekhon.minimaltodo.database.AppDatabase
import com.example.avjindersinghsekhon.minimaltodo.database.Todo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDb: AppDatabase
) {

    fun getTodos(): Flow<List<Todo>> = todoDb.todoDao().getAll()

    fun loadById(id: UUID): Flow<Todo> = todoDb.todoDao().loadById(id)

    fun insertAll(vararg todos: Todo): Flow<Unit> = flow {
        todoDb.todoDao().insertAll(*todos)
        emit(Unit)
    }

    fun delete(todo: Todo): Flow<Unit> = flow {
        todoDb.todoDao().delete(todo)
        emit(Unit)
    }

    fun deleteById(identifier: UUID): Flow<Unit> = flow {
        todoDb.todoDao().deleteById(identifier)
        emit(Unit)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateTodo(id: UUID, date: Date): Flow<Unit> = loadById(id).flatMapConcat { todo ->
        todo.date = date
        todo.hasReminder = true
        insertAll(todo)
    }
}