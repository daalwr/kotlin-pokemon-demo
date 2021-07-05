package dev.danielwright.main

import jakarta.validation.constraints.Min
import kotlinx.serialization.Serializable

@Serializable
data class GetPokemonByIdRequest(

    @field:Min(2)
    val id: Int

)
