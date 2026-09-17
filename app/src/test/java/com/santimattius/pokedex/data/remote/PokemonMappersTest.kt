package com.santimattius.pokedex.data.remote

import com.santimattius.pokedex.data.remote.dto.OfficialArtworkDto
import com.santimattius.pokedex.data.remote.dto.OtherSpritesDto
import com.santimattius.pokedex.data.remote.dto.PokemonResponse
import com.santimattius.pokedex.data.remote.dto.SpritesDto
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonMappersTest {

    @Test
    fun `toDomain converts weight in hectograms and height in decimetres to kg and m`() {
        val response = pokemonResponse(weight = 905, height = 17)

        val result = response.toDomain()

        assertEquals(90.5, result.weightKg, 0.0)
        assertEquals(1.7, result.heightM, 0.0)
    }

    @Test
    fun `toDomain converts boundary weight and height values without extra rounding`() {
        val response = pokemonResponse(weight = 1, height = 0)

        val result = response.toDomain()

        assertEquals(0.1, result.weightKg, 0.0)
        assertEquals(0.0, result.heightM, 0.0)
    }

    private fun pokemonResponse(weight: Int, height: Int): PokemonResponse = PokemonResponse(
        id = 25,
        name = "pikachu",
        weight = weight,
        height = height,
        sprites = SpritesDto(
            other = OtherSpritesDto(
                officialArtwork = OfficialArtworkDto(frontDefault = "https://example.com/25.png"),
            ),
        ),
        types = emptyList(),
        stats = emptyList(),
    )
}
