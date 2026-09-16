package com.santimattius.pokedex.data.repository

import com.santimattius.pokedex.data.remote.PokemonSpeciesService
import com.santimattius.pokedex.data.remote.dto.HabitatDto
import com.santimattius.pokedex.data.remote.dto.PokemonSpeciesResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonSpeciesRepositoryTest {

    private val pikachuResponse = PokemonSpeciesResponse(
        name = "pikachu",
        captureRate = 190,
        isLegendary = false,
        isMythical = false,
        habitat = HabitatDto("forest"),
    )

    @Test
    fun `getSpecies lowercases the name before calling the service`() = runTest {
        val service = mockk<PokemonSpeciesService> {
            coEvery { getPokemonSpecies("pikachu") } returns pikachuResponse
        }
        val repository = PokemonSpeciesRepository(service)

        repository.getSpecies("Pikachu")

        coVerify(exactly = 1) { service.getPokemonSpecies("pikachu") }
    }

    @Test
    fun `getSpecies maps the service response into a domain PokemonSpecies`() = runTest {
        val service = mockk<PokemonSpeciesService> {
            coEvery { getPokemonSpecies("pikachu") } returns pikachuResponse
        }
        val repository = PokemonSpeciesRepository(service)

        val result = repository.getSpecies("pikachu")

        assertEquals("pikachu", result.name)
        assertEquals(190, result.captureRate)
        assertEquals(false, result.isLegendary)
        assertEquals(false, result.isMythical)
        assertEquals("forest", result.habitat)
    }
}
