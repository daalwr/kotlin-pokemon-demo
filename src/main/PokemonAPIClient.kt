package dev.danielwright.main

import io.ktor.client.request.*

class PokemonAPIClient(val client: PokemonHttpClient) {
    suspend fun getPokemon(number: Int): Pokemon {
        return client.getClient().get("https://pokeapi.co/api/v2/pokemon/$number")
    }
}