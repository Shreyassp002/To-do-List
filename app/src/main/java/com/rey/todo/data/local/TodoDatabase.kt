package com.rey.todo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rey.todo.data.local.converters.DateConverter
import com.rey.todo.data.local.model.Todo

@TypeConverters(value = [DateConverter::class])
@Database(
    entities = [Todo::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDao: TodoDao
}