package com.rey.todo.domain.use_cases

import com.rey.todo.data.local.model.Todo
import com.rey.todo.domain.repository.Repository
import javax.inject.Inject

class AddUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(todo: Todo) = repository.insert(todo)
}