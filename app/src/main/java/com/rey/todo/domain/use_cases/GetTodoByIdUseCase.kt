package com.rey.todo.domain.use_cases

import com.rey.todo.domain.repository.Repository
import javax.inject.Inject

class GetTodoByIdUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(id:Long) = repository.getTodoById(id)
}