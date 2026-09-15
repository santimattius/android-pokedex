package com.santimattius.pokedex.di

import com.santimattius.pokedex.core.networking.RetrofitServiceCreator
import com.santimattius.pokedex.data.remote.PokemonService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providePokemonService(creator: RetrofitServiceCreator): PokemonService =
        creator.create<PokemonService>()
}
