package com.example.avjindersinghsekhon.minimaltodo.di

import android.content.Context
import androidx.room.Room
import com.example.avjindersinghsekhon.minimaltodo.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideTodoDB(@ApplicationContext context : Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "todo-db").build()
}