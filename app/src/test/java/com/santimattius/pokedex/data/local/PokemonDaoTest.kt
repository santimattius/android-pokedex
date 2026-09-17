package com.santimattius.pokedex.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.time.Instant

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class PokemonDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: PokemonDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.pokemonDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `findByName returns null when no entity was ever stored`() = runTest {
        val result = dao.findByName("pikachu")

        assertNull(result)
    }

    @Test
    fun `upsert then findByName returns the stored entity`() = runTest {
        val entity = PokemonEntity(
            name = "pikachu",
            payload = """{"id":25,"name":"pikachu"}""",
            updatedAt = Instant.ofEpochMilli(1_000_000L),
        )

        dao.upsert(entity)
        val result = dao.findByName("pikachu")

        assertEquals(entity, result)
    }

    @Test
    fun `upsert replaces an existing entity with the same name`() = runTest {
        val original = PokemonEntity(
            name = "pikachu",
            payload = """{"id":25,"name":"pikachu"}""",
            updatedAt = Instant.ofEpochMilli(1_000_000L),
        )
        val updated = original.copy(
            payload = """{"id":25,"name":"pikachu","hp":40}""",
            updatedAt = Instant.ofEpochMilli(2_000_000L),
        )

        dao.upsert(original)
        dao.upsert(updated)
        val result = dao.findByName("pikachu")

        assertEquals(updated, result)
    }
}
