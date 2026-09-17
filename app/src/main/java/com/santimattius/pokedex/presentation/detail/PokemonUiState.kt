package com.santimattius.pokedex.presentation.detail

sealed interface PokemonUiState {
    data object Idle : PokemonUiState
    data object Loading : PokemonUiState
    data class Content(val pokemon: PokemonUiModel) : PokemonUiState
    data class Error(val cause: Throwable) : PokemonUiState
}
