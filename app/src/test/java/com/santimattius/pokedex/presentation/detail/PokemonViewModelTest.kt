package com.santimattius.pokedex.presentation.detail

import com.santimattius.pokedex.data.remote.dto.OfficialArtworkDto
import com.santimattius.pokedex.data.remote.dto.OtherSpritesDto
import com.santimattius.pokedex.data.remote.dto.PokemonResponse
import com.santimattius.pokedex.data.remote.dto.SpritesDto
import com.santimattius.pokedex.data.remote.dto.StatDto
import com.santimattius.pokedex.data.remote.dto.StatSlotDto
import com.santimattius.pokedex.data.remote.dto.TypeDto
import com.santimattius.pokedex.data.remote.dto.TypeSlotDto
import com.santimattius.pokedex.data.remote.toDomain
import com.santimattius.pokedex.domain.CaptureDifficulty
import com.santimattius.pokedex.domain.GetPokemonProfile
import com.santimattius.pokedex.domain.PokemonProfile
import com.santimattius.pokedex.domain.PokemonSpecies
import com.santimattius.pokedex.tools.rules.MainCoroutinesTestRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    private val mapper = PokemonUiMapper()

    private val pikachuResponse = PokemonResponse(
        id = 25,
        name = "pikachu",
        weight = 60,
        height = 4,
        sprites = SpritesDto(OtherSpritesDto(OfficialArtworkDto("https://example.com/25.png"))),
        types = listOf(TypeSlotDto(TypeDto("electric"))),
        stats = listOf(StatSlotDto(baseStat = 35, stat = StatDto("hp"))),
    )

    private val pikachuSpecies = PokemonSpecies(
        name = "pikachu",
        habitat = "forest",
        captureRate = 190,
        isLegendary = false,
        isMythical = false,
    )

    private val pikachuProfile = PokemonProfile(
        pokemon = pikachuResponse.toDomain(),
        species = pikachuSpecies,
        totalBaseStats = 35,
        captureDifficulty = CaptureDifficulty.EASY,
        isSpecial = false,
    )

    @Test
    fun `initial state is Idle`() = runTest(mainCoroutinesTestRule.testDispatcher) {
        val getPokemonProfile = mockk<GetPokemonProfile>()
        val viewModel = PokemonViewModel("pikachu", getPokemonProfile, mapper)

        assertEquals(PokemonUiState.Idle, viewModel.state.value)
    }

    @Test
    fun `load emits Loading before Content on success`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val getPokemonProfile = mockk<GetPokemonProfile>()
            coEvery { getPokemonProfile("pikachu") } coAnswers {
                delay(100)
                pikachuProfile
            }
            val viewModel = PokemonViewModel("pikachu", getPokemonProfile, mapper)

            val states = mutableListOf<PokemonUiState>()
            val job = launch { viewModel.state.toList(states) }

            advanceUntilIdle()
            job.cancel()

            assertTrue(states.any { it is PokemonUiState.Loading })
            val loadingIndex = states.indexOfFirst { it is PokemonUiState.Loading }
            val contentIndex = states.indexOfFirst { it is PokemonUiState.Content }
            assertTrue(loadingIndex < contentIndex)
            val content = states.last() as PokemonUiState.Content
            assertEquals("Pikachu", content.pokemon.displayName)
        }

    @Test
    fun `load normalizes a mixed-case name to lowercase before calling the interactor`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val getPokemonProfile = mockk<GetPokemonProfile>()
            coEvery { getPokemonProfile("pikachu") } returns pikachuProfile
            val viewModel = PokemonViewModel("Pikachu", getPokemonProfile, mapper)

            val job = launch { viewModel.state.collect {} }
            advanceUntilIdle()
            job.cancel()

            coVerify(exactly = 1) { getPokemonProfile("pikachu") }
        }

    @Test
    fun `load emits Error when the interactor throws`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val failure = RuntimeException("not found")
            val getPokemonProfile = mockk<GetPokemonProfile>()
            coEvery { getPokemonProfile("missingno") } coAnswers {
                delay(100)
                throw failure
            }
            val viewModel = PokemonViewModel("missingno", getPokemonProfile, mapper)

            val states = mutableListOf<PokemonUiState>()
            val job = launch { viewModel.state.toList(states) }

            advanceUntilIdle()
            job.cancel()

            val loadingIndex = states.indexOfFirst { it is PokemonUiState.Loading }
            val errorIndex = states.indexOfFirst { it is PokemonUiState.Error }
            assertTrue(loadingIndex < errorIndex)
            val error = states.last() as PokemonUiState.Error
            assertEquals(failure, error.cause)
        }

    @Test
    fun `load emits Content carrying totalBaseStats, captureDifficulty and isSpecial from the profile`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val getPokemonProfile = mockk<GetPokemonProfile>()
            coEvery { getPokemonProfile("pikachu") } returns pikachuProfile
            val viewModel = PokemonViewModel("pikachu", getPokemonProfile, mapper)

            val job = launch { viewModel.state.collect {} }
            advanceUntilIdle()
            job.cancel()

            val content = viewModel.state.value as PokemonUiState.Content
            assertEquals(35, content.pokemon.totalBaseStats)
            assertEquals("EASY", content.pokemon.captureDifficulty)
            assertEquals(false, content.pokemon.isSpecial)
        }

    @Test
    fun `refresh re-invokes the interactor with the same name`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val getPokemonProfile = mockk<GetPokemonProfile>()
            coEvery { getPokemonProfile("pikachu") } returns pikachuProfile
            val viewModel = PokemonViewModel("pikachu", getPokemonProfile, mapper)

            val job = launch { viewModel.state.collect {} }
            advanceUntilIdle()
            viewModel.refresh()
            advanceUntilIdle()
            job.cancel()

            coVerify(exactly = 2) { getPokemonProfile("pikachu") }
        }
}
