package com.santimattius.pokedex.mockwebserver

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

interface ExampleService {

    @GET("/platform")
    suspend fun platform(): Platform
}

data class Platform(
    @SerializedName("platform") val platform: String
)