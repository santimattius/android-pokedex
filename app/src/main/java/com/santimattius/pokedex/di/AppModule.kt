package com.santimattius.pokedex.di

import com.santimattius.pokedex.core.networking.RetrofitServiceCreator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val POKE_API_BASE_URL = "https://pokeapi.co/api/v2/"

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofitCreator(): RetrofitServiceCreator {
        return RetrofitServiceCreator(baseUrl = POKE_API_BASE_URL)
    }
}