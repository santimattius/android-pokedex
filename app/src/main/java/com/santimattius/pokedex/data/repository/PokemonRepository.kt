package com.santimattius.pokedex.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.santimattius.pokedex.data.local.PokemonDao
import com.santimattius.pokedex.data.local.toDomain
import com.santimattius.pokedex.data.local.toEntity
import com.santimattius.pokedex.data.paging.PokemonPagingSource
import com.santimattius.pokedex.data.remote.PokemonService
import com.santimattius.pokedex.data.remote.toDomain
import com.santimattius.pokedex.domain.Pokemon
import com.santimattius.pokedex.domain.PokemonSummary
import java.time.Clock
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration
import kotlinx.coroutines.flow.Flow

private val CACHE_TTL = 24.hours.toJavaDuration()
private const val PAGE_SIZE = 20

class PokemonRepository @Inject constructor(
    private val service: PokemonService,
    private val dao: PokemonDao,
    private val clock: Clock,
    private val gson: Gson,
) {

    suspend fun getPokemon(name: String): Pokemon {
        val normalizedName = name.lowercase()
        val cached = dao.findByName(normalizedName)

        if (cached != null && isFresh(cached.updatedAt)) {
            return cached.toDomain(gson)
        }

        return runCatching {
            service.getPokemon(normalizedName).toDomain()
        }.onSuccess { pokemon ->
            dao.upsert(pokemon.toEntity(gson, updatedAt = clock.instant()))
        }.getOrElse { error ->
            cached?.toDomain(gson) ?: throw error
        }
    }

    fun pokemonPage(): Flow<PagingData<PokemonSummary>> =
        Pager(
            PagingConfig(pageSize = PAGE_SIZE, initialLoadSize = PAGE_SIZE),
        ) { PokemonPagingSource(service) }.flow

    private fun isFresh(updatedAt: Instant): Boolean =
        Duration.between(updatedAt, clock.instant()) < CACHE_TTL
}
