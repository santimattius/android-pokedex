package com.santimattius.pokedex.presentation.detail

import com.santimattius.pokedex.domain.Pokemon
import com.santimattius.pokedex.domain.PokemonProfile
import com.santimattius.pokedex.domain.PokemonStat
import com.santimattius.pokedex.domain.PokemonType
import java.util.Locale
import javax.inject.Inject

private const val MAX_BASE_STAT = 255
private const val STAT_NAME_SEPARATOR = "-"
private const val STAT_LABEL_SEPARATOR = " "
private const val WEIGHT_FORMAT = "%.1f KG"
private const val HEIGHT_FORMAT = "%.1f M"

class PokemonUiMapper @Inject constructor() {

    fun map(pokemon: Pokemon): PokemonUiModel = PokemonUiModel(
        id = pokemon.id,
        displayName = pokemon.name.replaceFirstChar(Char::uppercase),
        imageUrl = pokemon.officialArtworkUrl,
        weightLabel = String.format(Locale.US, WEIGHT_FORMAT, pokemon.weightKg),
        heightLabel = String.format(Locale.US, HEIGHT_FORMAT, pokemon.heightM),
        types = pokemon.types.map { it.toUiModel() },
        stats = pokemon.stats.map { it.toUiModel() },
    )

    fun map(profile: PokemonProfile): PokemonUiModel = map(profile.pokemon).copy(
        totalBaseStats = profile.totalBaseStats,
        captureDifficulty = profile.captureDifficulty.name,
        isSpecial = profile.isSpecial,
    )

    private fun PokemonType.toUiModel(): PokemonTypeUiModel = PokemonTypeUiModel(
        label = name.uppercase(),
        style = PokemonTypeStyle.from(name),
    )

    private fun PokemonStat.toUiModel(): PokemonStatUiModel = PokemonStatUiModel(
        label = name.toStatLabel(),
        value = baseValue,
        progress = (baseValue / MAX_BASE_STAT.toFloat()).coerceIn(0f, 1f),
        valueLabel = "$baseValue/$MAX_BASE_STAT",
    )

    private fun String.toStatLabel(): String =
        split(STAT_NAME_SEPARATOR).joinToString(STAT_LABEL_SEPARATOR) { word ->
            word.replaceFirstChar(Char::uppercase)
        }
}
