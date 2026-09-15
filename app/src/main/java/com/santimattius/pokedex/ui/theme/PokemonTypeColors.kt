package com.santimattius.pokedex.ui.theme

import androidx.compose.ui.graphics.Color
import com.santimattius.pokedex.presentation.detail.PokemonTypeStyle

val PokemonTypeStyle.containerColor: Color
    get() = when (this) {
        PokemonTypeStyle.NORMAL -> Color(0xFFA8A878)
        PokemonTypeStyle.FIRE -> Color(0xFFF08030)
        PokemonTypeStyle.WATER -> Color(0xFF6890F0)
        PokemonTypeStyle.GRASS -> Color(0xFF78C850)
        PokemonTypeStyle.ELECTRIC -> Color(0xFFF8D030)
        PokemonTypeStyle.ICE -> Color(0xFF98D8D8)
        PokemonTypeStyle.FIGHTING -> Color(0xFFC03028)
        PokemonTypeStyle.POISON -> Color(0xFFA040A0)
        PokemonTypeStyle.GROUND -> Color(0xFFE0C068)
        PokemonTypeStyle.FLYING -> Color(0xFFA890F0)
        PokemonTypeStyle.PSYCHIC -> Color(0xFFF85888)
        PokemonTypeStyle.BUG -> Color(0xFFA8B820)
        PokemonTypeStyle.ROCK -> Color(0xFFB8A038)
        PokemonTypeStyle.GHOST -> Color(0xFF705898)
        PokemonTypeStyle.DRAGON -> Color(0xFF7038F8)
        PokemonTypeStyle.DARK -> Color(0xFF705848)
        PokemonTypeStyle.STEEL -> Color(0xFFB8B8D0)
        PokemonTypeStyle.FAIRY -> Color(0xFFEE99AC)
        PokemonTypeStyle.UNKNOWN -> Color(0xFF68A090)
    }
