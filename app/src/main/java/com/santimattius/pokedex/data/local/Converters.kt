package com.santimattius.pokedex.data.local

import androidx.room.TypeConverter
import java.time.Instant

class Converters {

    @TypeConverter
    fun fromEpochMilli(epochMilli: Long?): Instant? = epochMilli?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun toEpochMilli(instant: Instant?): Long? = instant?.toEpochMilli()
}
