package com.santimattius.pokedex.domain

data class PokemonProfile(
    val pokemon: Pokemon,
    val species: PokemonSpecies,
    val totalBaseStats: Int,
    val captureDifficulty: CaptureDifficulty,
    val isSpecial: Boolean,
)
