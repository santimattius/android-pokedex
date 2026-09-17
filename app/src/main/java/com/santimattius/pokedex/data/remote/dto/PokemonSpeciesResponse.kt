package com.santimattius.pokedex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonSpeciesResponse(
    val name: String,
    @SerializedName("capture_rate")
    val captureRate: Int,
    @SerializedName("is_legendary")
    val isLegendary: Boolean,
    @SerializedName("is_mythical")
    val isMythical: Boolean,
    val habitat: HabitatDto?,
)

data class HabitatDto(
    val name: String,
)
