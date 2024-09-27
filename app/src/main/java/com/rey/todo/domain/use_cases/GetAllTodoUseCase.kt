package com.rey.todo.domain.use_cases

import com.rey.todo.data.local.model.Todo
import com.rey.todo.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTodoUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(): Flow<List<Todo>> =
        repository.getAllTodos()
}