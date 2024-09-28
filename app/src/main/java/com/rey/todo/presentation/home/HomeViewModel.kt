package com.rey.todo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rey.todo.common.ScreenViewState
import com.rey.todo.data.local.model.Todo
import com.rey.todo.domain.use_cases.AddUseCase
import com.rey.todo.domain.use_cases.DeleteTodoUseCase
import com.rey.todo.domain.use_cases.GetAllTodoUseCase
import com.rey.todo.domain.use_cases.UpdateTodoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllTodoUseCase: GetAllTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val addUseCase: AddUseCase, // Use AddUseCase instead of CreateTodoUseCase
) : ViewModel() {
    private val _state: MutableStateFlow<HomeState> = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        getAllTodos()
    }

    private fun getAllTodos() {
        getAllTodoUseCase()
            .onEach {
                _state.value = HomeState(todos = ScreenViewState.Success(it))
            }
            .catch {
                _state.value = HomeState(todos = ScreenViewState.Error(it.message))
            }
            .launchIn(viewModelScope)
    }

    fun deleteTodo(todoId: Long) = viewModelScope.launch {
        deleteTodoUseCase(todoId)
        getAllTodos() // Call getAllTodos after deleting a todo
    }

    fun updateTodoOrder(newOrder: List<Todo>) {
        // Call your use case to update the order in the database
        // After that, update the state to reflect the new order
        _state.value = HomeState(todos = ScreenViewState.Success(newOrder))
    }

    fun createTodo(newTodo: Todo) = viewModelScope.launch {
        addUseCase(newTodo) // Use AddUseCase to add the new todo
        getAllTodos() // Call getAllTodos after creating a new todo
    }
}

data class HomeState(
    val todos: ScreenViewState<List<Todo>> = ScreenViewState.Loading,
)