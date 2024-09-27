package com.rey.todo.di

import android.content.Context
import androidx.room.Room
import com.rey.todo.data.local.TodoDao
import com.rey.todo.data.local.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideNoteDao(database: TodoDatabase): TodoDao =
        database.todoDao

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): TodoDatabase = Room.databaseBuilder(
        context,
        TodoDatabase::class.java,
        "todo_db"
    )
        .build()
}