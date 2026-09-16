package com.santimattius.pokedex.presentation.list

import com.santimattius.pokedex.domain.PokemonSummary
import javax.inject.Inject

private const val ARTWORK_URL_TEMPLATE =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon" +
        "/other/official-artwork/%d.png"

class PokemonListUiMapper @Inject constructor() {

    fun map(summary: PokemonSummary): PokemonListItemUiModel = PokemonListItemUiModel(
        name = summary.name,
        displayName = summary.name.replaceFirstChar(Char::uppercase),
        imageUrl = ARTWORK_URL_TEMPLATE.format(summary.id),
        accentStyle = accentStyleFor(summary.id),
    )
}
