package com.rey.todo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rey.todo.data.local.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao{
    @Query("SELECT * FROM todos ORDER BY createdDate")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id=:id ORDER BY createdDate")
    fun getTodoById(id: Long): Flow<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(todo: Todo)

    @Query("DELETE FROM todos WHERE id=:id")
    suspend fun delete(id: Long)

}