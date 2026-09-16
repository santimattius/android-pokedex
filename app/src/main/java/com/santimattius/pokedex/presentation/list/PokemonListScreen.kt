package com.santimattius.pokedex.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.santimattius.pokedex.ui.theme.containerColor

private val ItemPadding = 8.dp
private val ArtworkSize = 96.dp
private val CardCornerRadius = 16.dp
private const val GRID_COLUMNS = 2

object PokemonListTestTags {
    const val LIST = "pokemon_list"
    fun item(name: String): String = "pokemon_list_item_$name"
    fun artwork(name: String): String = "pokemon_list_artwork_$name"
}

@Composable
fun PokemonListRoute(
    onSelect: (String) -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel<PokemonListViewModel>(),
) {
    val items = viewModel.items.collectAsLazyPagingItems()
    PokemonListScreen(items = items, onSelect = onSelect)
}

@Composable
fun PokemonListScreen(
    items: LazyPagingItems<PokemonListItemUiModel>,
    onSelect: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(PokemonListTestTags.LIST),
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey { it.name },
        ) { index ->
            val item = items[index]
            if (item != null) {
                PokemonListCard(item = item, onSelect = onSelect)
            }
        }
    }
}

@Composable
private fun PokemonListCard(item: PokemonListItemUiModel, onSelect: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(CardCornerRadius),
        color = item.accentStyle.containerColor,
        modifier = Modifier
            .padding(ItemPadding)
            .clickable { onSelect(item.name) }
            .testTag(PokemonListTestTags.item(item.name)),
    ) {
        Column(
            modifier = Modifier.padding(ItemPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.displayName,
                modifier = Modifier
                    .size(ArtworkSize)
                    .testTag(PokemonListTestTags.artwork(item.name)),
            )
            Text(text = item.displayName)
        }
    }
}
