package com.santimattius.pokedex.presentation.list

import com.santimattius.pokedex.presentation.detail.PokemonTypeStyle

data class PokemonListItemUiModel(
    val name: String,
    val displayName: String,
    val imageUrl: String,
    val accentStyle: PokemonTypeStyle,
)
