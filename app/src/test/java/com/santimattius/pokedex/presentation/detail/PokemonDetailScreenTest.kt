package com.santimattius.pokedex.presentation.detail

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.pokedex.ui.component.BasicSkeletonContainer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class PokemonDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val pikachu = PokemonUiModel(
        id = 25,
        displayName = "Pikachu",
        imageUrl = "https://example.com/25.png",
        types = listOf(PokemonTypeUiModel(label = "ELECTRIC", style = PokemonTypeStyle.ELECTRIC)),
        stats = listOf(PokemonStatUiModel(label = "Hp", value = 35, progress = 35 / 255f)),
    )

    @Test
    fun `loading state shows the loading indicator`() {
        composeTestRule.setContent {
            BasicSkeletonContainer {
                PokemonDetailScreen(state = PokemonUiState.Loading, onRetry = {})
            }
        }

        composeTestRule.onNodeWithTag(PokemonDetailTestTags.LOADING).assertExists()
    }

    @Test
    fun `content state shows the pokemon name instead of the loading indicator`() {
        composeTestRule.setContent {
            BasicSkeletonContainer {
                PokemonDetailScreen(state = PokemonUiState.Content(pikachu), onRetry = {})
            }
        }

        composeTestRule.onNodeWithTag(PokemonDetailTestTags.CONTENT).assertExists()
        composeTestRule.onNodeWithText("Pikachu").assertExists()
        composeTestRule.onNodeWithText("ELECTRIC").assertExists()
        composeTestRule.onNodeWithText("Hp: 35").assertExists()
        composeTestRule.onNodeWithTag(PokemonDetailTestTags.LOADING).assertDoesNotExist()
    }

    @Test
    fun `error state shows the error tag and invokes onRetry when tapped`() {
        var retried = false

        composeTestRule.setContent {
            BasicSkeletonContainer {
                PokemonDetailScreen(
                    state = PokemonUiState.Error(RuntimeException("boom")),
                    onRetry = { retried = true },
                )
            }
        }

        composeTestRule.onNodeWithTag(PokemonDetailTestTags.ERROR).assertExists()
        composeTestRule.onNodeWithTag(PokemonDetailTestTags.RETRY_BUTTON).performClick()

        assert(retried) { "Expected onRetry to be invoked after tapping the retry button" }
    }
}
