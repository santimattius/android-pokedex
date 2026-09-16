package com.santimattius.pokedex.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonProfileMapperTest {

    private val mapper = PokemonProfileMapper()

    private fun pokemon(stats: List<PokemonStat> = listOf(PokemonStat("hp", 35), PokemonStat("attack", 55))): Pokemon =
        Pokemon(
            id = 25,
            name = "pikachu",
            officialArtworkUrl = "https://example.com/25.png",
            types = listOf(PokemonType("electric")),
            stats = stats,
        )

    private fun species(
        captureRate: Int = 190,
        isLegendary: Boolean = false,
        isMythical: Boolean = false,
    ): PokemonSpecies = PokemonSpecies(
        name = "pikachu",
        habitat = "forest",
        captureRate = captureRate,
        isLegendary = isLegendary,
        isMythical = isMythical,
    )

    @Test
    fun `map classifies a captureRate of 180 as Easy`() {
        val result = mapper.map(pokemon(), species(captureRate = 180))

        assertEquals(CaptureDifficulty.EASY, result.captureDifficulty)
    }

    @Test
    fun `map classifies a captureRate of 179 as Medium`() {
        val result = mapper.map(pokemon(), species(captureRate = 179))

        assertEquals(CaptureDifficulty.MEDIUM, result.captureDifficulty)
    }

    @Test
    fun `map classifies a captureRate of 90 as Medium`() {
        val result = mapper.map(pokemon(), species(captureRate = 90))

        assertEquals(CaptureDifficulty.MEDIUM, result.captureDifficulty)
    }

    @Test
    fun `map classifies a captureRate of 89 as Hard`() {
        val result = mapper.map(pokemon(), species(captureRate = 89))

        assertEquals(CaptureDifficulty.HARD, result.captureDifficulty)
    }

    @Test
    fun `map sets isSpecial true when legendary is true and mythical is false`() {
        val result = mapper.map(pokemon(), species(isLegendary = true, isMythical = false))

        assertEquals(true, result.isSpecial)
    }

    @Test
    fun `map sets isSpecial true when legendary is false and mythical is true`() {
        val result = mapper.map(pokemon(), species(isLegendary = false, isMythical = true))

        assertEquals(true, result.isSpecial)
    }

    @Test
    fun `map sets isSpecial true when both legendary and mythical are true`() {
        val result = mapper.map(pokemon(), species(isLegendary = true, isMythical = true))

        assertEquals(true, result.isSpecial)
    }

    @Test
    fun `map sets isSpecial false when neither legendary nor mythical`() {
        val result = mapper.map(pokemon(), species(isLegendary = false, isMythical = false))

        assertEquals(false, result.isSpecial)
    }

    @Test
    fun `map sums every stat baseValue into totalBaseStats`() {
        val result = mapper.map(
            pokemon(stats = listOf(PokemonStat("hp", 35), PokemonStat("attack", 55), PokemonStat("defense", 40))),
            species(),
        )

        assertEquals(130, result.totalBaseStats)
    }

    @Test
    fun `map carries the original pokemon and species through unchanged`() {
        val pokemon = pokemon()
        val species = species()

        val result = mapper.map(pokemon, species)

        assertEquals(pokemon, result.pokemon)
        assertEquals(species, result.species)
    }
}
