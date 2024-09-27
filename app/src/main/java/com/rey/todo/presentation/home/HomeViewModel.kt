package com.rey.todo.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rey.todo.common.ScreenViewState
import com.rey.todo.data.local.model.Todo
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

    fun deleteNote(todoId:Long) = viewModelScope.launch {
        deleteTodoUseCase(todoId)
    }

}

data class HomeState(
    val todos: ScreenViewState<List<Todo>> = ScreenViewState.Loading,
)