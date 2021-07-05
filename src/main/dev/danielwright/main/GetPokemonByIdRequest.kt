package dev.danielwright.main

import kotlinx.serialization.Serializable

@Serializable
data class GetPokemonByIdRequest(val id: Int)
