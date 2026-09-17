package com.santimattius.pokedex.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santimattius.pokedex.data.remote.PokemonService
import com.santimattius.pokedex.data.remote.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val service: PokemonService,
) : ViewModel() {

    private val _state = MutableStateFlow<PokemonUiState>(PokemonUiState.Idle)
    val state: StateFlow<PokemonUiState> = _state.asStateFlow()

    fun load(name: String) {
        viewModelScope.launch {
            _state.value = PokemonUiState.Loading
            _state.value = runCatching {
                service.getPokemon(name.lowercase()).toDomain()
            }.fold(
                onSuccess = PokemonUiState::Content,
                onFailure = PokemonUiState::Error,
            )
        }
    }
}
