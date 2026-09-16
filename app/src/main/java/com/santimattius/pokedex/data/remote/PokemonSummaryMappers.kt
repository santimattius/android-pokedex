package com.santimattius.pokedex.data.remote

import com.santimattius.pokedex.data.remote.dto.PokemonPageItem
import com.santimattius.pokedex.domain.PokemonSummary

private const val URL_PATH_SEPARATOR = "/"

fun PokemonPageItem.toDomain(): PokemonSummary = PokemonSummary(
    id = url.trimEnd('/').substringAfterLast(URL_PATH_SEPARATOR).toInt(),
    name = name,
)
