package com.santimattius.pokedex.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface PokedexRoute : NavKey {

    @Serializable
    data object List : PokedexRoute

    @Serializable
    data class Detail(val name: String) : PokedexRoute
}
