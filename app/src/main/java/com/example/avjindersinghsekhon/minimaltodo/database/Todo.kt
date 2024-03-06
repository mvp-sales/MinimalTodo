package com.example.avjindersinghsekhon.minimaltodo.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String? = null,
    var description: String? = null,
    var hasReminder: Boolean = false,
    var date: Date? = null,
    val identifier: UUID = UUID.randomUUID()
)