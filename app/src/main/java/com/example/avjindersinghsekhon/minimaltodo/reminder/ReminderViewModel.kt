package com.example.avjindersinghsekhon.minimaltodo.reminder

import androidx.lifecycle.ViewModel
import com.example.avjindersinghsekhon.minimaltodo.database.Todo
import com.example.avjindersinghsekhon.minimaltodo.repositories.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {

    fun getTodo(identifier: UUID) =
            repository.loadById(identifier).flowOn(Dispatchers.IO)

    fun updateTodo(id: UUID, date: Date) =
            repository.updateTodo(id, date).flowOn(Dispatchers.IO)

    fun deleteTodo(todoId: UUID): Flow<Unit> =
            repository.deleteById(todoId).flowOn(Dispatchers.IO)
}