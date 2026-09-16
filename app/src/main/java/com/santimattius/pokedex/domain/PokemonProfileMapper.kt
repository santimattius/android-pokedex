package com.santimattius.pokedex.domain

import javax.inject.Inject

private const val EASY_CAPTURE_RATE = 180
private const val MEDIUM_CAPTURE_RATE = 90

class PokemonProfileMapper @Inject constructor() {

    fun map(pokemon: Pokemon, species: PokemonSpecies): PokemonProfile = PokemonProfile(
        pokemon = pokemon,
        species = species,
        totalBaseStats = pokemon.stats.sumOf { it.baseValue },
        captureDifficulty = species.captureRate.toCaptureDifficulty(),
        isSpecial = species.isLegendary || species.isMythical,
    )

    private fun Int.toCaptureDifficulty(): CaptureDifficulty = when {
        this >= EASY_CAPTURE_RATE -> CaptureDifficulty.EASY
        this >= MEDIUM_CAPTURE_RATE -> CaptureDifficulty.MEDIUM
        else -> CaptureDifficulty.HARD
    }
}
