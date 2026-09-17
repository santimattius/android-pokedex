package com.santimattius.pokedex.domain

import com.santimattius.pokedex.data.repository.PokemonRepository
import com.santimattius.pokedex.data.repository.PokemonSpeciesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

private const val FETCH_DELAY_MILLIS = 1_000L

@OptIn(ExperimentalCoroutinesApi::class)
class GetPokemonProfileTest {

    private val mapper = PokemonProfileMapper()

    private fun pokemon(): Pokemon = Pokemon(
        id = 25,
        name = "pikachu",
        officialArtworkUrl = "https://example.com/25.png",
        weightKg = 6.0,
        heightM = 0.4,
        types = listOf(PokemonType("electric")),
        stats = listOf(PokemonStat("hp", 35), PokemonStat("attack", 55)),
    )

    private fun species(): PokemonSpecies = PokemonSpecies(
        name = "pikachu",
        habitat = "forest",
        captureRate = 190,
        isLegendary = false,
        isMythical = false,
    )

    @Test
    fun `invoke aggregates pokemon and species into a profile with the summed totalBaseStats`() = runTest {
        val pokemonRepository = mockk<PokemonRepository> {
            coEvery { getPokemon("pikachu") } returns pokemon()
        }
        val speciesRepository = mockk<PokemonSpeciesRepository> {
            coEvery { getSpecies("pikachu") } returns species()
        }
        val getPokemonProfile = GetPokemonProfile(pokemonRepository, speciesRepository, mapper)

        val result = getPokemonProfile("pikachu")

        assertEquals(90, result.totalBaseStats)
    }

    @Test
    fun `invoke fetches pokemon and species concurrently`() = runTest {
        val pokemonRepository = mockk<PokemonRepository> {
            coEvery { getPokemon("pikachu") } coAnswers {
                delay(FETCH_DELAY_MILLIS)
                pokemon()
            }
        }
        val speciesRepository = mockk<PokemonSpeciesRepository> {
            coEvery { getSpecies("pikachu") } coAnswers {
                delay(FETCH_DELAY_MILLIS)
                species()
            }
        }
        val getPokemonProfile = GetPokemonProfile(pokemonRepository, speciesRepository, mapper)

        getPokemonProfile("pikachu")

        assertEquals(FETCH_DELAY_MILLIS, currentTime)
    }

    @Test
    fun `invoke propagates a failure from the pokemon repository without returning a profile`() {
        val failure = RuntimeException("pokemon fetch failed")
        val pokemonRepository = mockk<PokemonRepository> {
            coEvery { getPokemon("missingno") } throws failure
        }
        val speciesRepository = mockk<PokemonSpeciesRepository> {
            coEvery { getSpecies("missingno") } returns species()
        }
        val getPokemonProfile = GetPokemonProfile(pokemonRepository, speciesRepository, mapper)

        val thrown = assertThrows(RuntimeException::class.java) {
            runTest { getPokemonProfile("missingno") }
        }

        assertEquals(failure.message, thrown.message)
    }

    @Test
    fun `invoke propagates a failure from the species repository without returning a profile`() {
        val failure = RuntimeException("species fetch failed")
        val pokemonRepository = mockk<PokemonRepository> {
            coEvery { getPokemon("missingno") } returns pokemon()
        }
        val speciesRepository = mockk<PokemonSpeciesRepository> {
            coEvery { getSpecies("missingno") } throws failure
        }
        val getPokemonProfile = GetPokemonProfile(pokemonRepository, speciesRepository, mapper)

        val thrown = assertThrows(RuntimeException::class.java) {
            runTest { getPokemonProfile("missingno") }
        }

        assertEquals(failure.message, thrown.message)
    }
}
