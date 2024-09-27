package com.rey.todo.data.repository

import com.rey.todo.data.local.TodoDao
import com.rey.todo.data.local.model.Todo
import com.rey.todo.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao,
) : Repository {
    override fun getAllTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos()
    }

    override fun getTodoById(id: Long): Flow<Todo> {
        return todoDao.getTodoById(id)
    }

    override suspend fun insert(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    override suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    override suspend fun delete(id: Long) {
        todoDao.delete(id)
    }

}