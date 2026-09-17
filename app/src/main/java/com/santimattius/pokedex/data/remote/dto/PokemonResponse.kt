package com.santimattius.pokedex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonResponse(
    val id: Int,
    val name: String,
    val sprites: SpritesDto,
    val types: List<TypeSlotDto>,
    val stats: List<StatSlotDto>,
)

data class SpritesDto(
    val other: OtherSpritesDto,
)

data class OtherSpritesDto(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtworkDto,
)

data class OfficialArtworkDto(
    @SerializedName("front_default")
    val frontDefault: String,
)

data class TypeSlotDto(
    val type: TypeDto,
)

data class TypeDto(
    val name: String,
)

data class StatSlotDto(
    @SerializedName("base_stat")
    val baseStat: Int,
    val stat: StatDto,
)

data class StatDto(
    val name: String,
)
