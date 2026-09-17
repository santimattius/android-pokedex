package com.santimattius.pokedex.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.santimattius.pokedex.ui.component.BasicSkeletonContainer
import com.santimattius.pokedex.ui.theme.containerColor

internal const val DEFAULT_POKEMON_NAME = "pikachu"
private val ContentPadding = 16.dp
private val ChipPadding = 12.dp
private val ArtworkSize = 120.dp

object PokemonDetailTestTags {
    const val LOADING = "pokemon_detail_loading"
    const val CONTENT = "pokemon_detail_content"
    const val ERROR = "pokemon_detail_error"
    const val RETRY_BUTTON = "pokemon_detail_retry"
    const val ARTWORK = "pokemon_detail_artwork"
}

@Composable
fun PokemonDetailRoute(
    viewModel: PokemonViewModel,
    onBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PokemonDetailScreen(
        state = state,
        onRetry = viewModel::refresh,
        onBack = onBack,
    )
}

@Composable
fun PokemonDetailScreen(
    state: PokemonUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is PokemonUiState.Idle -> Unit
            is PokemonUiState.Loading -> LoadingContent()
            is PokemonUiState.Content -> PokemonContent(state.pokemon, onBack = onBack)
            is PokemonUiState.Error -> ErrorContent(onRetry = onRetry)
        }
    }
}

@Composable
private fun LoadingContent() {
    CircularProgressIndicator(modifier = Modifier.testTag(PokemonDetailTestTags.LOADING))
}

@Composable
private fun PokemonContent(pokemon: PokemonUiModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(PokemonDetailTestTags.CONTENT),
    ) {
        PokemonHeader(pokemon = pokemon, onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ContentPadding),
        ) {
            TypeChips(pokemon)
            WeightHeightRow(pokemon)
            Text(text = "Base Stats")
            pokemon.stats.forEach { stat -> StatRow(stat = stat, style = pokemon.primaryStyle) }
        }
    }
}

@Composable
private fun PokemonHeader(pokemon: PokemonUiModel, onBack: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(pokemon.primaryStyle.containerColor),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = pokemon.imageUrl,
            contentDescription = pokemon.displayName,
            modifier = Modifier
                .size(ArtworkSize)
                .testTag(PokemonDetailTestTags.ARTWORK),
        )
        Text(text = pokemon.displayName)
        Text(text = "#${pokemon.id}")
    }


}

@Composable
private fun TypeChips(pokemon: PokemonUiModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(ChipPadding)) {
        pokemon.types.forEach { type ->
            Surface(
                shape = CircleShape,
                color = type.style.containerColor,
            ) {
                Text(
                    text = type.label,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = ChipPadding),
                )
            }
        }
    }
}

@Composable
private fun WeightHeightRow(pokemon: PokemonUiModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Column {
            Text(text = "Weight")
            Text(text = pokemon.weightLabel)
        }
        Column {
            Text(text = "Height")
            Text(text = pokemon.heightLabel)
        }
    }
}

@Composable
private fun StatRow(stat: PokemonStatUiModel, style: PokemonTypeStyle) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        Text(text = stat.label)
        LinearProgressIndicator(
            progress = { stat.progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ChipPadding)
                .weight(1f),
            color = style.containerColor,
        )
        Text(text = stat.valueLabel)
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
                PokemonUiModel(
                    id = 25,
                    displayName = "Pikachu",
                    imageUrl = "",
                    weightLabel = "6.0 KG",
                    heightLabel = "0.4 M",
                    types = listOf(
                        PokemonTypeUiModel(label = "ELECTRIC", style = PokemonTypeStyle.ELECTRIC),
                    ),
                    stats = listOf(
                        PokemonStatUiModel(
                            label = "Hp",
                            value = 35,
                            progress = 35 / 255f,
                            valueLabel = "35/255"
                        ),
                    ),
                ),
            ),
            onRetry = {},
        )
    }
}
