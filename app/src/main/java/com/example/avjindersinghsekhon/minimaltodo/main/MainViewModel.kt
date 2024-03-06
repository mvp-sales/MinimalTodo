package com.example.avjindersinghsekhon.minimaltodo.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avjindersinghsekhon.minimaltodo.database.Todo
import com.example.avjindersinghsekhon.minimaltodo.repositories.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {
    val todosFlow: Flow<List<Todo>>
        get() = repository.getTodos().flowOn(Dispatchers.IO)

    fun deleteTodo(item: Todo): Flow<Unit> = repository.delete(item).flowOn(Dispatchers.IO)
}