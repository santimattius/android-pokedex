package com.santimattius.pokedex.presentation.detail

import com.santimattius.pokedex.domain.Pokemon
import com.santimattius.pokedex.domain.PokemonStat
import com.santimattius.pokedex.domain.PokemonType
import javax.inject.Inject

private const val MAX_BASE_STAT = 255f
private const val STAT_NAME_SEPARATOR = "-"
private const val STAT_LABEL_SEPARATOR = " "

class PokemonUiMapper @Inject constructor() {

    fun map(pokemon: Pokemon): PokemonUiModel = PokemonUiModel(
        id = pokemon.id,
        displayName = pokemon.name.replaceFirstChar(Char::uppercase),
        imageUrl = pokemon.officialArtworkUrl,
        types = pokemon.types.map { it.toUiModel() },
        stats = pokemon.stats.map { it.toUiModel() },
    )

    private fun PokemonType.toUiModel(): PokemonTypeUiModel = PokemonTypeUiModel(
        label = name.uppercase(),
        style = PokemonTypeStyle.from(name),
    )

    private fun PokemonStat.toUiModel(): PokemonStatUiModel = PokemonStatUiModel(
        label = name.toStatLabel(),
        value = baseValue,
        progress = (baseValue / MAX_BASE_STAT).coerceIn(0f, 1f),
    )

    private fun String.toStatLabel(): String =
        split(STAT_NAME_SEPARATOR).joinToString(STAT_LABEL_SEPARATOR) { word ->
            word.replaceFirstChar(Char::uppercase)
        }
}
