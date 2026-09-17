package com.santimattius.pokedex.presentation.list

import com.santimattius.pokedex.presentation.detail.PokemonTypeStyle

private val ACCENT_STYLES = listOf(
    PokemonTypeStyle.GRASS,
    PokemonTypeStyle.FIRE,
    PokemonTypeStyle.WATER,
    PokemonTypeStyle.ELECTRIC,
    PokemonTypeStyle.PSYCHIC,
    PokemonTypeStyle.ROCK,
    PokemonTypeStyle.GHOST,
    PokemonTypeStyle.FAIRY,
)

internal fun accentStyleFor(id: Int): PokemonTypeStyle =
    ACCENT_STYLES[id.mod(ACCENT_STYLES.size)]
