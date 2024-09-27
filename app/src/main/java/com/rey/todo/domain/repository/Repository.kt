package com.rey.todo.domain.repository

import com.rey.todo.data.local.model.Todo
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getAllTodos(): Flow<List<Todo>>
    fun getTodoById(id: Long): Flow<Todo>
    suspend fun insert(todo: Todo)
    suspend fun update(todo: Todo)
    suspend fun delete(id: Long)

}