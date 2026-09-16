package com.santimattius.pokedex.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey

private val ItemPadding = 16.dp

object PokemonListTestTags {
    const val LIST = "pokemon_list"
    fun item(name: String): String = "pokemon_list_item_$name"
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
    LazyColumn(
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ItemPadding)
                        .clickable { onSelect(item.name) }
                        .testTag(PokemonListTestTags.item(item.name)),
                ) {
                    Text(text = item.displayName)
                }
            }
        }
    }
}
