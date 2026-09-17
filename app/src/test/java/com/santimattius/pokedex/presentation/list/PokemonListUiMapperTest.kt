package com.santimattius.pokedex.presentation.list

import com.santimattius.pokedex.domain.PokemonSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class PokemonListUiMapperTest {

    private val mapper = PokemonListUiMapper()

    @Test
    fun `map capitalizes the display name from a lowercase summary name`() {
        val summary = PokemonSummary(id = 25, name = "pikachu")

        val result = mapper.map(summary)

        assertEquals("Pikachu", result.displayName)
    }

    @Test
    fun `map keeps the raw name and derives the artwork url from the id`() {
        val summary = PokemonSummary(id = 25, name = "pikachu")

        val result = mapper.map(summary)

        assertEquals("pikachu", result.name)
        assertEquals(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon" +
                "/other/official-artwork/25.png",
            result.imageUrl,
        )
    }

    @Test
    fun `map never exposes type or stat fields`() {
        val summary = PokemonSummary(id = 1, name = "bulbasaur")

        val result = mapper.map(summary)

        val fieldNames = PokemonListItemUiModel::class.java.declaredFields.map { it.name }
        assertFalse(fieldNames.any { it.contains("type", ignoreCase = true) })
        assertFalse(fieldNames.any { it.contains("stat", ignoreCase = true) })
    }
}
