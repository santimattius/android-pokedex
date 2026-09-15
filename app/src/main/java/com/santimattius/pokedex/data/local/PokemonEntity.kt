package com.santimattius.pokedex.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val name: String,
    val payload: String,
    val updatedAt: Instant,
)
