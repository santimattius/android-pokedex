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

class PokemonSpeciesServiceTest {

    private val jsonLoader = JsonLoader()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PokemonSpeciesService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val baseUrl = mockWebServer.url("/").toUri().toString()
        val serviceCreator = RetrofitServiceCreator(baseUrl)
        service = serviceCreator.create<PokemonSpeciesService>()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getPokemonSpecies requests the pokemon-species path with the given name`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(jsonLoader.load("species_pikachu.json"))
        mockWebServer.enqueue(response)

        runBlocking {
            service.getPokemonSpecies("pikachu")
        }

        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.path, equalTo("/pokemon-species/pikachu"))
    }

    @Test
    fun `getPokemonSpecies maps the response body to a PokemonSpeciesResponse`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(jsonLoader.load("species_pikachu.json"))
        mockWebServer.enqueue(response)

        val result = runBlocking { service.getPokemonSpecies("pikachu") }

        assertThat(result.name, equalTo("pikachu"))
        assertThat(result.captureRate, equalTo(190))
        assertThat(result.isLegendary, equalTo(false))
        assertThat(result.isMythical, equalTo(false))
        assertThat(result.habitat?.name, equalTo("forest"))
    }

    @Test
    fun `toDomain maps a PokemonSpeciesResponse into a PokemonSpecies`() {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(jsonLoader.load("species_pikachu.json"))
        mockWebServer.enqueue(response)

        val dto = runBlocking { service.getPokemonSpecies("pikachu") }
        val species = dto.toDomain()

        assertThat(species.name, equalTo("pikachu"))
        assertThat(species.captureRate, equalTo(190))
        assertThat(species.isLegendary, equalTo(false))
        assertThat(species.isMythical, equalTo(false))
        assertThat(species.habitat, equalTo("forest"))
    }
}
