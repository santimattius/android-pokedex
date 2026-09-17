package com.santimattius.pokedex.domain

import com.santimattius.pokedex.data.repository.PokemonRepository
import com.santimattius.pokedex.data.repository.PokemonSpeciesRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetPokemonProfile @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val speciesRepository: PokemonSpeciesRepository,
    private val mapper: PokemonProfileMapper,
) {

    suspend operator fun invoke(name: String): PokemonProfile = coroutineScope {
        val normalizedName = name.lowercase()
        val pokemonDeferred = async { pokemonRepository.getPokemon(normalizedName) }
        val speciesDeferred = async { speciesRepository.getSpecies(normalizedName) }
        mapper.map(pokemonDeferred.await(), speciesDeferred.await())
    }
}
