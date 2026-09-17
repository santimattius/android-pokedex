package com.santimattius.pokedex.data.repository

import com.santimattius.pokedex.data.remote.PokemonSpeciesService
import com.santimattius.pokedex.data.remote.toDomain
import com.santimattius.pokedex.domain.PokemonSpecies
import javax.inject.Inject

class PokemonSpeciesRepository @Inject constructor(
    private val service: PokemonSpeciesService,
) {

    suspend fun getSpecies(name: String): PokemonSpecies =
        service.getPokemonSpecies(name.lowercase()).toDomain()
}
