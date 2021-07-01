package dev.danielwright

import io.ktor.client.request.*

class PokemonAPIClient {
    suspend fun getPokemon(number: Int): Pokemon {
        return client.get("https://pokeapi.co/api/v2/pokemon/$number")
    }
}