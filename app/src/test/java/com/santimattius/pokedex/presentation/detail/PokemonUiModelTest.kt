package com.santimattius.pokedex.presentation.detail

import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonUiModelTest {

    @Test
    fun `primaryStyle resolves to the first type's style ignoring secondary types`() {
        val pokemon = pokemonUiModel(
            types = listOf(
                PokemonTypeUiModel(label = "GRASS", style = PokemonTypeStyle.GRASS),
                PokemonTypeUiModel(label = "POISON", style = PokemonTypeStyle.POISON),
            ),
        )

        assertEquals(PokemonTypeStyle.GRASS, pokemon.primaryStyle)
    }

    @Test
    fun `primaryStyle falls back to UNKNOWN when there are no types`() {
        val pokemon = pokemonUiModel(types = emptyList())

        assertEquals(PokemonTypeStyle.UNKNOWN, pokemon.primaryStyle)
    }

    private fun pokemonUiModel(types: List<PokemonTypeUiModel>): PokemonUiModel = PokemonUiModel(
        id = 1,
        displayName = "Bulbasaur",
        imageUrl = "https://example.com/1.png",
        types = types,
        stats = emptyList(),
    )
}
