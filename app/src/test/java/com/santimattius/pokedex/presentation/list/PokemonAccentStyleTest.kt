package com.santimattius.pokedex.presentation.list

import com.santimattius.pokedex.presentation.detail.PokemonTypeStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PokemonAccentStyleTest {

    @Test
    fun `accentStyleFor resolves the same style for the same id across calls`() {
        val first = accentStyleFor(25)
        val second = accentStyleFor(25)

        assertEquals(first, second)
    }

    @Test
    fun `accentStyleFor resolves different styles for different ids`() {
        val zero = accentStyleFor(0)
        val one = accentStyleFor(1)

        assertNotEquals(zero, one)
    }

    @Test
    fun `accentStyleFor never returns the UNKNOWN fallback style`() {
        val styles = (0..20).map { accentStyleFor(it) }

        assertEquals(false, styles.contains(PokemonTypeStyle.UNKNOWN))
    }

    @Test
    fun `accentStyleFor does not throw for a negative id`() {
        val style = accentStyleFor(-5)

        assertEquals(style, accentStyleFor(-5))
    }
}
