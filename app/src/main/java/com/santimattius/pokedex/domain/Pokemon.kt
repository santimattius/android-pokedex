package com.santimattius.pokedex.domain

data class Pokemon(
    val id: Int,
    val name: String,
    val officialArtworkUrl: String,
    val weightKg: Double,
    val heightM: Double,
    val types: List<PokemonType>,
    val stats: List<PokemonStat>,
)

data class PokemonType(
    val name: String,
)

data class PokemonStat(
    val name: String,
    val baseValue: Int,
)
