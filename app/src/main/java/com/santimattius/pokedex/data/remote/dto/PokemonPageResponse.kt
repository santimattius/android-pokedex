package com.santimattius.pokedex.data.remote.dto

data class PokemonPageResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonPageItem>,
)

data class PokemonPageItem(
    val name: String,
    val url: String,
)
