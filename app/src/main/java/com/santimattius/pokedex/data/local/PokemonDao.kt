package com.santimattius.pokedex.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon WHERE name = :name")
    suspend fun findByName(name: String): PokemonEntity?

    @Upsert
    suspend fun upsert(pokemon: PokemonEntity)
}
