package com.santimattius.pokedex.presentation.detail

import com.santimattius.pokedex.domain.Pokemon
import com.santimattius.pokedex.domain.PokemonStat
import com.santimattius.pokedex.domain.PokemonType
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonUiMapperTest {

    private val mapper = PokemonUiMapper()

    @Test
    fun `map capitalizes the display name from a lowercase pokemon name`() {
        val pokemon = pokemon(name = "pikachu")

        val result = mapper.map(pokemon)

        assertEquals("Pikachu", result.displayName)
    }

    @Test
    fun `map keeps id and image url unchanged`() {
        val pokemon = pokemon(id = 25, officialArtworkUrl = "https://example.com/25.png")

        val result = mapper.map(pokemon)

        assertEquals(25, result.id)
        assertEquals("https://example.com/25.png", result.imageUrl)
    }

    @Test
    fun `map uppercases each type label and resolves its style from the type name`() {
        val pokemon = pokemon(types = listOf(PokemonType("electric"), PokemonType("fire")))

        val result = mapper.map(pokemon)

        assertEquals(
            listOf("ELECTRIC", "FIRE"),
            result.types.map { it.label },
        )
        assertEquals(
            listOf(PokemonTypeStyle.ELECTRIC, PokemonTypeStyle.FIRE),
            result.types.map { it.style },
        )
    }

    @Test
    fun `map resolves an unrecognized type name to the UNKNOWN style`() {
        val pokemon = pokemon(types = listOf(PokemonType("shadow")))

        val result = mapper.map(pokemon)

        assertEquals(PokemonTypeStyle.UNKNOWN, result.types.single().style)
    }

    @Test
    fun `map formats a hyphenated stat name into title-cased words`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "special-attack", baseValue = 50)))

        val result = mapper.map(pokemon)

        assertEquals("Special Attack", result.stats.single().label)
    }

    @Test
    fun `map normalizes a zero base stat to zero progress`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "hp", baseValue = 0)))

        val result = mapper.map(pokemon)

        assertEquals(0f, result.stats.single().progress)
    }

    @Test
    fun `map normalizes a base stat of 255 to full progress`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "hp", baseValue = 255)))

        val result = mapper.map(pokemon)

        assertEquals(1f, result.stats.single().progress)
    }

    @Test
    fun `map coerces a base stat above 255 to full progress instead of overflowing`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "hp", baseValue = 300)))

        val result = mapper.map(pokemon)

        assertEquals(1f, result.stats.single().progress)
    }

    private fun pokemon(
        id: Int = 1,
        name: String = "bulbasaur",
        officialArtworkUrl: String = "https://example.com/1.png",
        types: List<PokemonType> = listOf(PokemonType("grass")),
        stats: List<PokemonStat> = listOf(PokemonStat("hp", 45)),
    ): Pokemon = Pokemon(
        id = id,
        name = name,
        officialArtworkUrl = officialArtworkUrl,
        types = types,
        stats = stats,
    )
}
