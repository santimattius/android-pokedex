package com.santimattius.pokedex.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santimattius.pokedex.domain.GetPokemonProfile
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PokemonViewModel.Factory::class)
class PokemonViewModel @AssistedInject constructor(
    @Assisted private val name: String,
    private val getPokemonProfile: GetPokemonProfile,
    private val mapper: PokemonUiMapper,
) : ViewModel() {

    private val _state = MutableStateFlow<PokemonUiState>(PokemonUiState.Idle)
    val state: StateFlow<PokemonUiState> = _state.onStart {
        load(name)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        PokemonUiState.Idle
    )

    fun refresh() {
        load(name)
    }

    private fun load(name: String) {
        viewModelScope.launch {
            _state.value = PokemonUiState.Loading
            _state.value = runCatching {
                getPokemonProfile(name.lowercase())
            }.fold(
                onSuccess = { profile -> PokemonUiState.Content(mapper.map(profile)) },
                onFailure = PokemonUiState::Error,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(name: String): PokemonViewModel
    }

}
