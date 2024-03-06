package com.example.avjindersinghsekhon.minimaltodo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo")
    fun getAll(): Flow<List<Todo>>

    @Query("SELECT * FROM todo WHERE identifier = (:id)")
    fun loadById(id: UUID): Flow<List<Todo>>

    @Insert
    fun insertAll(vararg users: Todo)

    @Delete
    fun delete(user: Todo)
}