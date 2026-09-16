package com.santimattius.pokedex.presentation.list

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.pokedex.ui.component.BasicSkeletonContainer
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class PokemonListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val pikachu = PokemonListItemUiModel(
        name = "pikachu",
        displayName = "Pikachu",
        imageUrl = "https://example.com/25.png",
        accentStyle = accentStyleFor(25),
    )

    @Test
    fun `each grid cell renders an artwork node tagged with the item name`() {
        composeTestRule.setContent {
            BasicSkeletonContainer {
                val items = flowOf(PagingData.from(listOf(pikachu))).collectAsLazyPagingItems()
                PokemonListScreen(items = items, onSelect = {})
            }
        }

        composeTestRule
            .onNodeWithTag(PokemonListTestTags.artwork("pikachu"), useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun `tapping a list item still invokes onSelect with its name`() {
        var selected: String? = null

        composeTestRule.setContent {
            BasicSkeletonContainer {
                val items = flowOf(PagingData.from(listOf(pikachu))).collectAsLazyPagingItems()
                PokemonListScreen(items = items, onSelect = { selected = it })
            }
        }

        composeTestRule.onNodeWithTag(PokemonListTestTags.item("pikachu")).performClick()

        assert(selected == "pikachu") { "Expected onSelect to be invoked with 'pikachu'" }
    }
}
