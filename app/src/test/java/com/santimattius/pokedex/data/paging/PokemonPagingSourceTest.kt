package com.santimattius.pokedex.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.santimattius.pokedex.data.remote.PokemonService
import com.santimattius.pokedex.data.remote.dto.PokemonPageItem
import com.santimattius.pokedex.data.remote.dto.PokemonPageResponse
import com.santimattius.pokedex.domain.PokemonSummary
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private const val PAGE_SIZE = 20

class PokemonPagingSourceTest {

    private val service = mockk<PokemonService>()
    private val pagingSource = PokemonPagingSource(service)

    @Test
    fun `load returns a page with prevKey and nextKey when a next link exists`() = runTest {
        coEvery { service.getPokemonPage(limit = PAGE_SIZE, offset = 20) } returns PokemonPageResponse(
            count = 100,
            next = "https://pokeapi.co/api/v2/pokemon?offset=40&limit=20",
            previous = "https://pokeapi.co/api/v2/pokemon?offset=0&limit=20",
            results = listOf(PokemonPageItem(name = "pikachu", url = "https://pokeapi.co/api/v2/pokemon/25/")),
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = 20, loadSize = PAGE_SIZE, placeholdersEnabled = false),
        )

        val page = result as PagingSource.LoadResult.Page
        assertEquals(0, page.prevKey)
        assertEquals(40, page.nextKey)
        assertEquals(listOf(25), page.data.map { it.id })
        assertEquals(listOf("pikachu"), page.data.map { it.name })
    }

    @Test
    fun `load returns a null prevKey when offset minus limit is negative`() = runTest {
        coEvery { service.getPokemonPage(limit = PAGE_SIZE, offset = 0) } returns PokemonPageResponse(
            count = 100,
            next = "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
            previous = null,
            results = emptyList(),
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = 0, loadSize = PAGE_SIZE, placeholdersEnabled = false),
        )

        val page = result as PagingSource.LoadResult.Page
        assertNull(page.prevKey)
    }

    @Test
    fun `load returns a null nextKey on the terminal page`() = runTest {
        coEvery { service.getPokemonPage(limit = PAGE_SIZE, offset = 80) } returns PokemonPageResponse(
            count = 100,
            next = null,
            previous = "https://pokeapi.co/api/v2/pokemon?offset=60&limit=20",
            results = emptyList(),
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(key = 80, loadSize = PAGE_SIZE, placeholdersEnabled = false),
        )

        val page = result as PagingSource.LoadResult.Page
        assertNull(page.nextKey)
    }

    @Test
    fun `load returns an Error result when the service call throws`() = runTest {
        val failure = RuntimeException("network down")
        coEvery { service.getPokemonPage(limit = PAGE_SIZE, offset = 0) } throws failure

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = PAGE_SIZE, placeholdersEnabled = false),
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(failure, (result as PagingSource.LoadResult.Error).throwable)
    }

    @Test
    fun `getRefreshKey returns the closest page prevKey plus the page size`() {
        val page = PagingSource.LoadResult.Page(
            data = (0 until PAGE_SIZE).map { PokemonSummary(id = it, name = "pokemon-$it") },
            prevKey = 0,
            nextKey = 40,
        )
        val state = PagingState(
            pages = listOf(page),
            anchorPosition = 5,
            config = PagingConfig(pageSize = PAGE_SIZE),
            leadingPlaceholderCount = 0,
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertEquals(20, refreshKey)
    }

    @Test
    fun `getRefreshKey returns null when no anchor position exists`() {
        val state = PagingState<Int, PokemonSummary>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = PAGE_SIZE),
            leadingPlaceholderCount = 0,
        )

        val refreshKey = pagingSource.getRefreshKey(state)

        assertNull(refreshKey)
    }
}
