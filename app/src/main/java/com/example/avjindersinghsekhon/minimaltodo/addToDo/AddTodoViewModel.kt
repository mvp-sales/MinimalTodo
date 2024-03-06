package com.example.avjindersinghsekhon.minimaltodo.addToDo

import androidx.lifecycle.ViewModel
import com.example.avjindersinghsekhon.minimaltodo.database.Todo
import com.example.avjindersinghsekhon.minimaltodo.repositories.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {

    fun getTodo(identifier: UUID) =
            repository.loadById(identifier).flowOn(Dispatchers.IO)

    fun addTodo(todo: Todo) =
            repository.insertAll(todo).flowOn(Dispatchers.IO)
}