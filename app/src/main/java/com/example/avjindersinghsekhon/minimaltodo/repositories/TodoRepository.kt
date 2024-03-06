package com.example.avjindersinghsekhon.minimaltodo.repositories

import com.example.avjindersinghsekhon.minimaltodo.database.AppDatabase
import com.example.avjindersinghsekhon.minimaltodo.database.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDb: AppDatabase
) {

    fun getTodos(): Flow<List<Todo>> = todoDb.todoDao().getAll()

    fun loadById(id: UUID): Flow<List<Todo>> = todoDb.todoDao().loadById(id)

    fun insertAll(vararg todos: Todo): Flow<Unit> = flow {
        todoDb.todoDao().insertAll(*todos)
        emit(Unit)
    }

    fun delete(todo: Todo): Flow<Unit> = flow {
        todoDb.todoDao().delete(todo)
        emit(Unit)
    }
}