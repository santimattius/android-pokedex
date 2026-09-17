package com.santimattius.pokedex.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.pokedex.presentation.detail.PokemonDetailScreen
import com.santimattius.pokedex.presentation.detail.PokemonDetailTestTags
import com.santimattius.pokedex.presentation.detail.PokemonStatUiModel
import com.santimattius.pokedex.presentation.detail.PokemonUiModel
import com.santimattius.pokedex.presentation.detail.PokemonUiState
import com.santimattius.pokedex.presentation.list.PokemonListItemUiModel
import com.santimattius.pokedex.presentation.list.accentStyleFor
import com.santimattius.pokedex.presentation.list.PokemonListScreen
import com.santimattius.pokedex.presentation.list.PokemonListTestTags
import com.santimattius.pokedex.ui.component.BasicSkeletonContainer
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class PokedexNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val pikachuListItem = PokemonListItemUiModel(
        name = "pikachu",
        displayName = "Pikachu",
        imageUrl = "https://example.com/25.png",
        accentStyle = accentStyleFor(25),
    )

    private fun uiModelFor(name: String): PokemonUiModel = PokemonUiModel(
        id = 25,
        displayName = name.replaceFirstChar(Char::uppercase),
        imageUrl = "https://example.com/25.png",
        types = emptyList(),
        stats = listOf(PokemonStatUiModel(label = "Hp", value = 35, progress = 35 / 255f)),
    )

    private lateinit var backStack: NavBackStack<NavKey>

    private fun setNavigationContent() {
        composeTestRule.setContent {
            BasicSkeletonContainer {
                backStack = rememberNavBackStack(PokedexRoute.List)
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<PokedexRoute.List> {
                            val items = flowOf(PagingData.from(listOf(pikachuListItem)))
                                .collectAsLazyPagingItems()
                            PokemonListScreen(
                                items = items,
                                onSelect = { name -> backStack.add(PokedexRoute.Detail(name)) },
                            )
                        }
                        entry<PokedexRoute.Detail> { key ->
                            PokemonDetailScreen(
                                state = PokemonUiState.Content(uiModelFor(key.name)),
                                onRetry = {},
                            )
                        }
                    },
                )
            }
        }
    }

    @Test
    fun `tapping a list item navigates to the detail screen showing its content`() {
        setNavigationContent()

        composeTestRule.onNodeWithTag(PokemonListTestTags.item("pikachu")).performClick()

        composeTestRule.onNodeWithTag(PokemonDetailTestTags.CONTENT).assertExists()
        composeTestRule.onNodeWithText("Pikachu").assertExists()
    }

    @Test
    fun `popping the back stack returns from the detail screen to the list`() {
        setNavigationContent()

        composeTestRule.onNodeWithTag(PokemonListTestTags.item("pikachu")).performClick()
        composeTestRule.onNodeWithTag(PokemonDetailTestTags.CONTENT).assertExists()

        composeTestRule.runOnIdle { backStack.removeLastOrNull() }

        composeTestRule.onNodeWithTag(PokemonListTestTags.LIST).assertExists()
        composeTestRule.onNodeWithTag(PokemonDetailTestTags.CONTENT).assertDoesNotExist()
    }
}
