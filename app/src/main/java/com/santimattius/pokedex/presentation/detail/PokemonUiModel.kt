package com.santimattius.pokedex.presentation.detail

data class PokemonUiModel(
    val id: Int,
    val displayName: String,
    val imageUrl: String,
    val types: List<PokemonTypeUiModel>,
    val stats: List<PokemonStatUiModel>,
)

data class PokemonTypeUiModel(
    val label: String,
    val style: PokemonTypeStyle,
)

data class PokemonStatUiModel(
    val label: String,
    val value: Int,
    val progress: Float,
)
