package com.rey.todo.domain.use_cases

import com.rey.todo.domain.repository.Repository
import javax.inject.Inject

class DeleteTodoUseCase @Inject constructor(
    private val repository: Repository,
) {
    suspend operator fun invoke(id: Long) = repository.delete(id)
}