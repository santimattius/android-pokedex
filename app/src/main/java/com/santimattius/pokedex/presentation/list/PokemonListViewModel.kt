package com.santimattius.pokedex.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.santimattius.pokedex.data.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    repository: PokemonRepository,
    mapper: PokemonListUiMapper,
) : ViewModel() {

    val items: Flow<PagingData<PokemonListItemUiModel>> = mappedPokemonPage(repository, mapper)
        .cachedIn(viewModelScope)
}

/**
 * Maps the repository's paged summaries into list UI models.
 * Extracted from [PokemonListViewModel.items] so the mapping logic is testable
 * without going through `cachedIn`'s viewModelScope-bound sharing.
 */
internal fun mappedPokemonPage(
    repository: PokemonRepository,
    mapper: PokemonListUiMapper,
): Flow<PagingData<PokemonListItemUiModel>> =
    repository.pokemonPage().map { pagingData -> pagingData.map(mapper::map) }
