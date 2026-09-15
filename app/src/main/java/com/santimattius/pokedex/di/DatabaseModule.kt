package com.santimattius.pokedex.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.santimattius.pokedex.data.local.AppDatabase
import com.santimattius.pokedex.data.local.PokemonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

private const val DATABASE_NAME = "pokedex.db"

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun providePokemonDao(database: AppDatabase): PokemonDao = database.pokemonDao()

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemUTC()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}
