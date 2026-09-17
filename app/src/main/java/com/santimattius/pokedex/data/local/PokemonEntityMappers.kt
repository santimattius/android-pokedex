package com.santimattius.pokedex.data.local

import com.google.gson.Gson
import com.santimattius.pokedex.domain.Pokemon
import java.time.Instant

fun PokemonEntity.toDomain(gson: Gson): Pokemon = gson.fromJson(payload, Pokemon::class.java)

fun Pokemon.toEntity(gson: Gson, updatedAt: Instant): PokemonEntity = PokemonEntity(
    name = name,
    payload = gson.toJson(this),
    updatedAt = updatedAt,
)
