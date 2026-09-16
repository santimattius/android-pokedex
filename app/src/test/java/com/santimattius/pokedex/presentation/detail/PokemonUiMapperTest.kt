package com.santimattius.pokedex.presentation.detail

import com.santimattius.pokedex.domain.CaptureDifficulty
import com.santimattius.pokedex.domain.Pokemon
import com.santimattius.pokedex.domain.PokemonProfile
import com.santimattius.pokedex.domain.PokemonSpecies
import com.santimattius.pokedex.domain.PokemonStat
import com.santimattius.pokedex.domain.PokemonType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class PokemonUiMapperTest {

    private val mapper = PokemonUiMapper()
    private lateinit var defaultLocale: Locale

    @Before
    fun setUp() {
        defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.GERMANY)
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `map formats weight and height using US locale regardless of the default locale`() {
        val pokemon = pokemon(weightKg = 90.5, heightM = 1.7)

        val result = mapper.map(pokemon)

        assertEquals("90.5 KG", result.weightLabel)
        assertEquals("1.7 M", result.heightLabel)
    }

    @Test
    fun `map capitalizes the display name from a lowercase pokemon name`() {
        val pokemon = pokemon(name = "pikachu")

        val result = mapper.map(pokemon)

        assertEquals("Pikachu", result.displayName)
    }

    @Test
    fun `map keeps id and image url unchanged`() {
        val pokemon = pokemon(id = 25, officialArtworkUrl = "https://example.com/25.png")

        val result = mapper.map(pokemon)

        assertEquals(25, result.id)
        assertEquals("https://example.com/25.png", result.imageUrl)
    }

    @Test
    fun `map uppercases each type label and resolves its style from the type name`() {
        val pokemon = pokemon(types = listOf(PokemonType("electric"), PokemonType("fire")))

        val result = mapper.map(pokemon)

        assertEquals(
            listOf("ELECTRIC", "FIRE"),
            result.types.map { it.label },
        )
        assertEquals(
            listOf(PokemonTypeStyle.ELECTRIC, PokemonTypeStyle.FIRE),
            result.types.map { it.style },
        )
    }

    @Test
    fun `map resolves an unrecognized type name to the UNKNOWN style`() {
        val pokemon = pokemon(types = listOf(PokemonType("shadow")))

        val result = mapper.map(pokemon)

        assertEquals(PokemonTypeStyle.UNKNOWN, result.types.single().style)
    }

    @Test
    fun `map formats a hyphenated stat name into title-cased words`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "special-attack", baseValue = 50)))

        val result = mapper.map(pokemon)

        assertEquals("Special Attack", result.stats.single().label)
    }

    @Test
    fun `map exposes a value over max fraction label matching the progress denominator`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "hp", baseValue = 168)))

        val result = mapper.map(pokemon)

        assertEquals("168/255", result.stats.single().valueLabel)
        assertEquals(168 / 255f, result.stats.single().progress)
    }

    @Test
    fun `map normalizes a zero base stat to zero progress`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "hp", baseValue = 0)))

        val result = mapper.map(pokemon)

        assertEquals(0f, result.stats.single().progress)
    }

    @Test
    fun `map normalizes a base stat of 255 to full progress`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "hp", baseValue = 255)))

        val result = mapper.map(pokemon)

        assertEquals(1f, result.stats.single().progress)
    }

    @Test
    fun `map coerces a base stat above 255 to full progress instead of overflowing`() {
        val pokemon = pokemon(stats = listOf(PokemonStat(name = "hp", baseValue = 300)))

        val result = mapper.map(pokemon)

        assertEquals(1f, result.stats.single().progress)
    }

    @Test
    fun `map profile extends the base ui model with totalBaseStats, captureDifficulty and isSpecial`() {
        val profile = PokemonProfile(
            pokemon = pokemon(name = "pikachu"),
            species = PokemonSpecies(
                name = "pikachu",
                habitat = "forest",
                captureRate = 190,
                isLegendary = false,
                isMythical = false,
            ),
            totalBaseStats = 90,
            captureDifficulty = CaptureDifficulty.EASY,
            isSpecial = false,
        )

        val result = mapper.map(profile)

        assertEquals("Pikachu", result.displayName)
        assertEquals(90, result.totalBaseStats)
        assertEquals("EASY", result.captureDifficulty)
        assertEquals(false, result.isSpecial)
    }

    private fun pokemon(
        id: Int = 1,
        name: String = "bulbasaur",
        officialArtworkUrl: String = "https://example.com/1.png",
        weightKg: Double = 6.9,
        heightM: Double = 0.7,
        types: List<PokemonType> = listOf(PokemonType("grass")),
        stats: List<PokemonStat> = listOf(PokemonStat("hp", 45)),
    ): Pokemon = Pokemon(
        id = id,
        name = name,
        officialArtworkUrl = officialArtworkUrl,
        weightKg = weightKg,
        heightM = heightM,
        types = types,
        stats = stats,
    )
}
