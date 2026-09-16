package com.santimattius.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.santimattius.pokedex.navigation.PokedexRoute
import com.santimattius.pokedex.presentation.detail.PokemonDetailRoute
import com.santimattius.pokedex.presentation.list.PokemonListRoute
import com.santimattius.pokedex.ui.component.BasicSkeletonContainer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicSkeletonContainer {
                val backStack = rememberNavBackStack(PokedexRoute.List)
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<PokedexRoute.List> {
                            PokemonListRoute(
                                onSelect = { name -> backStack.add(PokedexRoute.Detail(name)) },
                            )
                        }
                        entry<PokedexRoute.Detail> { key ->
                            PokemonDetailRoute(name = key.name)
                        }
                    },
                )
            }
        }
    }
}
