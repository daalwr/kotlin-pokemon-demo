package dev.danielwright.main.model.request

import jakarta.validation.constraints.Min
import kotlinx.serialization.Serializable

@Serializable
data class GetPokemonById(

    @field:Min(2)
    val id: Int

)
