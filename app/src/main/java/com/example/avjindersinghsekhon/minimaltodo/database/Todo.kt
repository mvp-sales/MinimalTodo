package com.example.avjindersinghsekhon.minimaltodo.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String?,
    val description: String?,
    val hasReminder: Boolean,
    val date: Date?,
    val identifier: UUID = UUID.randomUUID()
)