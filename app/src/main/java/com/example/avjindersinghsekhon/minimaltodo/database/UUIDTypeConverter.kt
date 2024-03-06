package com.example.avjindersinghsekhon.minimaltodo.database

import androidx.room.TypeConverter
import java.util.UUID

class UUIDTypeConverter {
    @TypeConverter
    fun toString(entity: UUID) = entity.toString()

    @TypeConverter
    fun fromString(serialized: String): UUID = UUID.fromString(serialized)
}