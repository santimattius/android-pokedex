package com.santimattius.pokedex.data.remote

import com.santimattius.pokedex.data.remote.dto.PokemonPageResponse
import com.santimattius.pokedex.data.remote.dto.PokemonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonService {

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): PokemonResponse

    @GET("pokemon")
    suspend fun getPokemonPage(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): PokemonPageResponse
}
