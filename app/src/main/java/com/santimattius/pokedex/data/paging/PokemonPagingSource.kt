package com.santimattius.pokedex.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.santimattius.pokedex.data.remote.PokemonService
import com.santimattius.pokedex.data.remote.toDomain
import com.santimattius.pokedex.domain.PokemonSummary

class PokemonPagingSource(
    private val service: PokemonService,
) : PagingSource<Int, PokemonSummary>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonSummary> {
        val offset = params.key ?: 0
        val limit = params.loadSize

        return runCatching {
            service.getPokemonPage(limit = limit, offset = offset)
        }.fold(
            onSuccess = { response ->
                LoadResult.Page(
                    data = response.results.map { it.toDomain() },
                    prevKey = (offset - limit).takeIf { it >= 0 },
                    nextKey = (offset + limit).takeIf { response.next != null },
                )
            },
            onFailure = { error -> LoadResult.Error(error) },
        )
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonSummary>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
        }
}
