package com.santimattius.pokedex.data.repository

import com.google.gson.Gson
import com.santimattius.pokedex.data.local.PokemonDao
import com.santimattius.pokedex.data.local.PokemonEntity
import com.santimattius.pokedex.data.remote.PokemonService
import com.santimattius.pokedex.data.remote.toDomain
import com.santimattius.pokedex.data.remote.dto.OfficialArtworkDto
import com.santimattius.pokedex.data.remote.dto.OtherSpritesDto
import com.santimattius.pokedex.data.remote.dto.PokemonResponse
import com.santimattius.pokedex.data.remote.dto.SpritesDto
import com.santimattius.pokedex.data.remote.dto.StatDto
import com.santimattius.pokedex.data.remote.dto.StatSlotDto
import com.santimattius.pokedex.data.remote.dto.PokemonPageItem
import com.santimattius.pokedex.data.remote.dto.PokemonPageResponse
import com.santimattius.pokedex.data.remote.dto.TypeDto
import com.santimattius.pokedex.data.remote.dto.TypeSlotDto
import androidx.paging.testing.asSnapshot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

private const val NOW_EPOCH_MILLI = 1_700_000_000_000L
private val NOW: Instant = Instant.ofEpochMilli(NOW_EPOCH_MILLI)
private val TWELVE_HOURS_AGO: Instant = NOW.minusSeconds(12L * 60 * 60)
private val TWENTY_FIVE_HOURS_AGO: Instant = NOW.minusSeconds(25L * 60 * 60)

class PokemonRepositoryTest {

    private val gson = Gson()
    private val fixedClock: Clock = Clock.fixed(NOW, ZoneOffset.UTC)

    private val pikachuResponse = PokemonResponse(
        id = 25,
        name = "pikachu",
        weight = 60,
        height = 4,
        sprites = SpritesDto(OtherSpritesDto(OfficialArtworkDto("https://example.com/25.png"))),
        types = listOf(TypeSlotDto(TypeDto("electric"))),
        stats = listOf(StatSlotDto(baseStat = 35, stat = StatDto("hp"))),
    )

    private fun cachedEntity(updatedAt: Instant): PokemonEntity {
        val pokemon = pikachuResponse.toDomain()
        return PokemonEntity(
            name = pokemon.name,
            payload = gson.toJson(pokemon),
            updatedAt = updatedAt,
        )
    }

    @Test
    fun `fresh cache hit returns cached entity without calling the network`() = runTest {
        val cached = cachedEntity(updatedAt = TWELVE_HOURS_AGO)
        val service = mockk<PokemonService>()
        val dao = mockk<PokemonDao> {
            coEvery { findByName("pikachu") } returns cached
        }
        val repository = PokemonRepository(service, dao, fixedClock, gson)

        val result = repository.getPokemon("pikachu")

        assertEquals("pikachu", result.name)
        coVerify(exactly = 0) { service.getPokemon(any()) }
    }

    @Test
    fun `stale cache triggers a refetch and upserts the entity at the current instant`() = runTest {
        val cached = cachedEntity(updatedAt = TWENTY_FIVE_HOURS_AGO)
        val service = mockk<PokemonService> {
            coEvery { getPokemon("pikachu") } returns pikachuResponse
        }
        val dao = mockk<PokemonDao> {
            coEvery { findByName("pikachu") } returns cached
            coEvery { upsert(any()) } returns Unit
        }
        val repository = PokemonRepository(service, dao, fixedClock, gson)

        val result = repository.getPokemon("pikachu")

        assertEquals("pikachu", result.name)
        coVerify(exactly = 1) { service.getPokemon("pikachu") }
        coVerify(exactly = 1) { dao.upsert(match { it.name == "pikachu" && it.updatedAt == NOW }) }
    }

    @Test
    fun `network failure falls back to the cached entity when one exists`() = runTest {
        val cached = cachedEntity(updatedAt = TWENTY_FIVE_HOURS_AGO)
        val service = mockk<PokemonService> {
            coEvery { getPokemon("pikachu") } throws RuntimeException("network down")
        }
        val dao = mockk<PokemonDao> {
            coEvery { findByName("pikachu") } returns cached
        }
        val repository = PokemonRepository(service, dao, fixedClock, gson)

        val result = repository.getPokemon("pikachu")

        assertEquals("pikachu", result.name)
        coVerify(exactly = 0) { dao.upsert(any()) }
    }

    @Test
    fun `network failure with no cache propagates the original exception`() {
        val failure = RuntimeException("network down")
        val service = mockk<PokemonService> {
            coEvery { getPokemon("missingno") } throws failure
        }
        val dao = mockk<PokemonDao> {
            coEvery { findByName("missingno") } returns null
        }
        val repository = PokemonRepository(service, dao, fixedClock, gson)

        val thrown = assertThrows(RuntimeException::class.java) {
            runTest { repository.getPokemon("missingno") }
        }

        assertEquals(failure.message, thrown.message)
    }

    @Test
    fun `pokemonPage streams mapped summaries paged through PokemonPagingSource`() = runTest {
        val service = mockk<PokemonService> {
            coEvery { getPokemonPage(limit = 20, offset = 0) } returns PokemonPageResponse(
                count = 1,
                next = null,
                previous = null,
                results = listOf(
                    PokemonPageItem(name = "pikachu", url = "https://pokeapi.co/api/v2/pokemon/25/"),
                ),
            )
        }
        val dao = mockk<PokemonDao>()
        val repository = PokemonRepository(service, dao, fixedClock, gson)

        val snapshot = repository.pokemonPage().asSnapshot()

        assertEquals(listOf(25), snapshot.map { it.id })
        assertEquals(listOf("pikachu"), snapshot.map { it.name })
    }
}
