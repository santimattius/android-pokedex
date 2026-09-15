package com.santimattius.pokedex.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import com.santimattius.pokedex.domain.Pokemon
import com.santimattius.pokedex.domain.PokemonStat
import com.santimattius.pokedex.domain.PokemonType
import com.santimattius.pokedex.ui.component.BasicSkeletonContainer

internal const val DEFAULT_POKEMON_NAME = "pikachu"
private val ContentPadding = 16.dp

object PokemonDetailTestTags {
    const val LOADING = "pokemon_detail_loading"
    const val CONTENT = "pokemon_detail_content"
    const val ERROR = "pokemon_detail_error"
    const val RETRY_BUTTON = "pokemon_detail_retry"
}

@Composable
fun PokemonDetailRoute(
    viewModel: PokemonViewModel = hiltViewModel<PokemonViewModel>(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val load = { viewModel.load(DEFAULT_POKEMON_NAME) }

    LaunchedEffect(Unit) { load() }

    PokemonDetailScreen(
        state = state,
        onRetry = load,
    )
}

@Composable
fun PokemonDetailScreen(
    state: PokemonUiState,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is PokemonUiState.Idle -> Unit
            is PokemonUiState.Loading -> LoadingContent()
            is PokemonUiState.Content -> PokemonContent(state.pokemon)
            is PokemonUiState.Error -> ErrorContent(onRetry = onRetry)
        }
    }
}

@Composable
private fun LoadingContent() {
    CircularProgressIndicator(modifier = Modifier.testTag(PokemonDetailTestTags.LOADING))
}

@Composable
private fun PokemonContent(pokemon: Pokemon) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ContentPadding)
            .testTag(PokemonDetailTestTags.CONTENT),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = pokemon.name)
        Text(text = pokemon.types.joinToString { it.name })
        Text(text = pokemon.stats.joinToString { "${it.name}:${it.baseValue}" })
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.testTag(PokemonDetailTestTags.ERROR),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Something went wrong")
        Button(
            onClick = onRetry,
            modifier = Modifier.testTag(PokemonDetailTestTags.RETRY_BUTTON),
        ) {
            Text(text = "Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PokemonDetailScreenContentPreview() {
    BasicSkeletonContainer {
        PokemonDetailScreen(
            state = PokemonUiState.Content(
                Pokemon(
                    id = 25,
                    name = "pikachu",
                    officialArtworkUrl = "",
                    types = listOf(PokemonType(name = "electric")),
                    stats = listOf(PokemonStat(name = "hp", baseValue = 35)),
                ),
            ),
            onRetry = {},
        )
    }
}
