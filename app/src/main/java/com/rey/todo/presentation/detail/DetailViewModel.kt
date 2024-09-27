package com.rey.todo.presentation.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rey.todo.data.local.model.Todo
import com.rey.todo.domain.use_cases.AddUseCase
import com.rey.todo.domain.use_cases.GetTodoByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class DetailViewModel @AssistedInject constructor(
    private val addUseCase: AddUseCase,
    private val getTodoByIdUseCase: GetTodoByIdUseCase,
    @Assisted private val todoId: Long,
) : ViewModel() {
    var state by mutableStateOf(DetailState())
        private set
    val isFormNotBlank: Boolean
        get() = state.title.isNotEmpty() &&
                state.content.isNotEmpty()
    private val todo: Todo
        get() = state.run {
            Todo(
                id = id,
                title = title,
                content = content,
                createdDate = createdDate
            )
        }

    init {
        initialize()
    }


    private fun initialize() {
        val isUpdatingTodo = todoId != -1L
        state = state.copy(isUpdatingTodo = isUpdatingTodo)
        if (isUpdatingTodo) {
            getTodoById()
        }
    }

    private fun getTodoById() = viewModelScope.launch {
        getTodoByIdUseCase(todoId).collectLatest { todo ->
            state = state.copy(
                id = todo.id,
                title = todo.title,
                content = todo.content,
                createdDate = todo.createdDate
            )
        }
    }

    fun onTitleChange(title: String) {
        state = state.copy(title = title)
    }

    fun onContentChange(content: String) {
        state = state.copy(content = content)
    }


    fun addOrUpdateTodo() = viewModelScope.launch {
        addUseCase(todo = todo)
    }


}

data class DetailState(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val createdDate: Date = Date(),
    val isUpdatingTodo: Boolean = false,
)


@Suppress("UNCHECKED_CAST")
class DetailedViewModelFactory(
    private val todoId: Long,
    private val assistedFactory: DetailAssistedFactory,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return assistedFactory.create(todoId) as T
    }
}

@AssistedFactory
interface DetailAssistedFactory {
    fun create(todoId: Long): DetailViewModel
}