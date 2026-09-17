package com.santimattius.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.santimattius.pokedex.navigation.PokedexRoute
import com.santimattius.pokedex.presentation.detail.PokemonDetailRoute
import com.santimattius.pokedex.presentation.detail.PokemonViewModel
import com.santimattius.pokedex.presentation.list.PokemonListRoute
import com.santimattius.pokedex.ui.component.AppBar
import com.santimattius.pokedex.ui.component.AppBarIconModel
import com.santimattius.pokedex.ui.component.BasicSkeletonContainer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicSkeletonContainer {
                val backStack = rememberNavBackStack(PokedexRoute.List)

                Scaffold(
                    topBar = {
                        AppBar(
                            navIcon = if (backStack.size > 1) {
                                AppBarIconModel(
                                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    action = { backStack.removeLastOrNull() }
                                )
                            } else null
                        )
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        NavDisplay(
                            backStack = backStack,
                            onBack = { backStack.removeLastOrNull() },
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            entryProvider = entryProvider {
                                entry<PokedexRoute.List> {
                                    PokemonListRoute(
                                        onSelect = { name -> backStack.add(PokedexRoute.Detail(name)) },
                                    )
                                }
                                entry<PokedexRoute.Detail> { key ->
                                    val viewModel =
                                        hiltViewModel<PokemonViewModel, PokemonViewModel.Factory>(
                                            creationCallback = { factory ->
                                                factory.create(key.name)
                                            }
                                        )
                                    PokemonDetailRoute(
                                        viewModel = viewModel,
                                        onBack = { backStack.removeLastOrNull() },
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
