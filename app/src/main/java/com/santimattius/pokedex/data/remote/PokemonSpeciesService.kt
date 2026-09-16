package com.santimattius.pokedex.data.remote

import com.santimattius.pokedex.data.remote.dto.PokemonSpeciesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonSpeciesService {

    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): PokemonSpeciesResponse
}
