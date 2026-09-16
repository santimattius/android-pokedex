package com.santimattius.pokedex

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.compose.ui.test.waitUntilDoesNotExist
import com.santimattius.pokedex.presentation.detail.PokemonDetailTestTags
import com.santimattius.pokedex.presentation.list.PokemonListTestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchingTheAppResolvesTheRealHiltGraphAndNavigatesFromListToDetail() {
        composeRule.waitUntilAtLeastOneExists(hasTestTag(PokemonListTestTags.LIST), timeoutMillis = 10_000)
        composeRule.onAllNodes(hasClickAction())[0].performClick()

        composeRule.waitUntilDoesNotExist(
            hasTestTag(PokemonDetailTestTags.LOADING),
            timeoutMillis = 10_000,
        )

        composeRule.onNodeWithTag(PokemonDetailTestTags.CONTENT).assertExists()
    }
}
