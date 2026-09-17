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
import com.santimattius.pokedex.data.repository.PokemonRepository
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
        sprites = SpritesDto(OtherSpritesDto(OfficialArtworkDto("https://example.com/25.png"))),
        types = listOf(TypeSlotDto(TypeDto("electric"))),
        stats = listOf(StatSlotDto(baseStat = 35, stat = StatDto("hp"))),
    )

    @Test
    fun `initial state is Idle`() = runTest(mainCoroutinesTestRule.testDispatcher) {
        val repository = mockk<PokemonRepository>()
        val viewModel = PokemonViewModel(repository, mapper)

        assertEquals(PokemonUiState.Idle, viewModel.state.value)
    }

    @Test
    fun `load emits Loading before Content on success`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val repository = mockk<PokemonRepository> {
                coEvery { getPokemon("pikachu") } coAnswers {
                    delay(100)
                    pikachuResponse.toDomain()
                }
            }
            val viewModel = PokemonViewModel(repository, mapper)

            val states = mutableListOf<PokemonUiState>()
            val job = launch { viewModel.state.toList(states) }

            viewModel.load("pikachu")
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
    fun `load normalizes a mixed-case name to lowercase before calling the repository`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val repository = mockk<PokemonRepository> {
                coEvery { getPokemon("pikachu") } returns pikachuResponse.toDomain()
            }
            val viewModel = PokemonViewModel(repository, mapper)

            viewModel.load("Pikachu")
            advanceUntilIdle()

            coVerify(exactly = 1) { repository.getPokemon("pikachu") }
        }

    @Test
    fun `load emits Error when the repository throws`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val failure = RuntimeException("not found")
            val repository = mockk<PokemonRepository> {
                coEvery { getPokemon("missingno") } coAnswers {
                    delay(100)
                    throw failure
                }
            }
            val viewModel = PokemonViewModel(repository, mapper)

            val states = mutableListOf<PokemonUiState>()
            val job = launch { viewModel.state.toList(states) }

            viewModel.load("missingno")
            advanceUntilIdle()
            job.cancel()

            val loadingIndex = states.indexOfFirst { it is PokemonUiState.Loading }
            val errorIndex = states.indexOfFirst { it is PokemonUiState.Error }
            assertTrue(loadingIndex < errorIndex)
            val error = states.last() as PokemonUiState.Error
            assertEquals(failure, error.cause)
        }
}
