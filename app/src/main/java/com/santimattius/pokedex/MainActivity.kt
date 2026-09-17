package com.santimattius.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.santimattius.pokedex.presentation.detail.PokemonDetailRoute
import com.santimattius.pokedex.ui.component.BasicSkeletonContainer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicSkeletonContainer {
                PokemonDetailRoute()
            }
        }
    }
}
