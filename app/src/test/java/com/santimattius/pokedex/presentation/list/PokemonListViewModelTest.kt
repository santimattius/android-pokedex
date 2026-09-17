package com.santimattius.pokedex.presentation.list

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.santimattius.pokedex.data.repository.PokemonRepository
import com.santimattius.pokedex.domain.PokemonSummary
import com.santimattius.pokedex.tools.rules.MainCoroutinesTestRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    private val mapper = PokemonListUiMapper()

    @Test
    fun `items maps each paged summary to a PokemonListItemUiModel`() =
        runTest(mainCoroutinesTestRule.testDispatcher) {
            val summaries = listOf(
                PokemonSummary(id = 1, name = "bulbasaur"),
                PokemonSummary(id = 25, name = "pikachu"),
            )
            val repository = mockk<PokemonRepository> {
                every { pokemonPage() } returns flowOf(PagingData.from(summaries))
            }
            val snapshot = mappedPokemonPage(repository, mapper).asSnapshot()

            assertEquals(listOf("Bulbasaur", "Pikachu"), snapshot.map { it.displayName })
            assertEquals(listOf("bulbasaur", "pikachu"), snapshot.map { it.name })
        }
}
