package com.santimattius.pokedex.presentation.detail

import com.santimattius.pokedex.domain.Pokemon

sealed interface PokemonUiState {
    data object Idle : PokemonUiState
    data object Loading : PokemonUiState
    data class Content(val pokemon: Pokemon) : PokemonUiState
    data class Error(val cause: Throwable) : PokemonUiState
}
