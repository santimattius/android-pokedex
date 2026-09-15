package com.santimattius.pokedex.data.remote

import com.santimattius.pokedex.data.remote.dto.PokemonResponse
import com.santimattius.pokedex.data.remote.dto.StatSlotDto
import com.santimattius.pokedex.data.remote.dto.TypeSlotDto
import com.santimattius.pokedex.domain.Pokemon
import com.santimattius.pokedex.domain.PokemonStat
import com.santimattius.pokedex.domain.PokemonType

fun PokemonResponse.toDomain(): Pokemon = Pokemon(
    id = id,
    name = name,
    officialArtworkUrl = sprites.other.officialArtwork.frontDefault,
    types = types.map(TypeSlotDto::toDomain),
    stats = stats.map(StatSlotDto::toDomain),
)

fun TypeSlotDto.toDomain(): PokemonType = PokemonType(name = type.name)

fun StatSlotDto.toDomain(): PokemonStat = PokemonStat(name = stat.name, baseValue = baseStat)
