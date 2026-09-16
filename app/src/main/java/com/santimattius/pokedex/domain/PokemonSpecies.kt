package com.santimattius.pokedex.domain

data class PokemonSpecies(
    val name: String,
    val habitat: String?,
    val captureRate: Int,
    val isLegendary: Boolean,
    val isMythical: Boolean,
)
