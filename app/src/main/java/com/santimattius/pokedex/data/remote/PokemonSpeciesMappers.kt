package com.santimattius.pokedex.data.remote

import com.santimattius.pokedex.data.remote.dto.PokemonSpeciesResponse
import com.santimattius.pokedex.domain.PokemonSpecies

fun PokemonSpeciesResponse.toDomain(): PokemonSpecies = PokemonSpecies(
    name = name,
    habitat = habitat?.name,
    captureRate = captureRate,
    isLegendary = isLegendary,
    isMythical = isMythical,
)
