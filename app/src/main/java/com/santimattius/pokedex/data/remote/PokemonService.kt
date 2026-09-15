package com.santimattius.pokedex.data.remote

import com.santimattius.pokedex.data.remote.dto.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonService {

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): PokemonResponse
}
