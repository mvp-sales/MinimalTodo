package com.example.avjindersinghsekhon.minimaltodo.database

import androidx.room.TypeConverter
import java.util.Date

class DateTypeConverter {

    @TypeConverter
    fun toLong(entity: Date) = entity.time

    @TypeConverter
    fun fromLong(serialized: Long) = Date(serialized)
}