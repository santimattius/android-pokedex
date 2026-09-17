package com.santimattius.pokedex.data.remote

import com.santimattius.pokedex.core.networking.RetrofitServiceCreator
import com.santimattius.pokedex.tools.helpers.JsonLoader
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection

private const val PIKACHU_ARTWORK_URL =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon" +
        "/other/official-artwork/25.png"

class PokemonServiceTest {

    private val jsonLoader = JsonLoader()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PokemonService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val baseUrl = mockWebServer.url("/").toUri().toString()
        val serviceCreator = RetrofitServiceCreator(baseUrl)
        service = serviceCreator.create<PokemonService>()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getPokemon requests the pokemon path with the given name`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(jsonLoader.load("pokemon_pikachu.json"))
        mockWebServer.enqueue(response)

        runBlocking {
            service.getPokemon("pikachu")
        }

        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.path, equalTo("/pokemon/pikachu"))
    }

    @Test
    fun `getPokemon maps the response body to a PokemonResponse`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(jsonLoader.load("pokemon_pikachu.json"))
        mockWebServer.enqueue(response)

        val result = runBlocking { service.getPokemon("pikachu") }

        assertThat(result.id, equalTo(25))
        assertThat(result.name, equalTo("pikachu"))
        assertThat(
            result.sprites.other.officialArtwork.frontDefault,
            equalTo(PIKACHU_ARTWORK_URL),
        )
        assertThat(result.types.map { it.type.name }, equalTo(listOf("electric")))
        assertThat(result.stats.map { it.stat.name to it.baseStat }, equalTo(listOf("hp" to 35, "attack" to 55)))
    }

    @Test
    fun `toDomain maps a PokemonResponse into a Pokemon with types and stats`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(jsonLoader.load("pokemon_pikachu.json"))
        mockWebServer.enqueue(response)

        val dto = runBlocking { service.getPokemon("pikachu") }
        val pokemon = dto.toDomain()

        assertThat(pokemon.id, equalTo(25))
        assertThat(pokemon.name, equalTo("pikachu"))
        assertThat(
            pokemon.officialArtworkUrl,
            equalTo(PIKACHU_ARTWORK_URL),
        )
        assertThat(pokemon.types.map { it.name }, equalTo(listOf("electric")))
        assertThat(pokemon.stats.map { it.name to it.baseValue }, equalTo(listOf("hp" to 35, "attack" to 55)))
        assertThat(pokemon.weightKg, equalTo(6.0))
        assertThat(pokemon.heightM, equalTo(0.4))
    }
}
