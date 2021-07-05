package dev.danielwright.main

import kotlin.random.Random

class PokemonService(private val pokemonAPIClient: PokemonAPIClient) {

    suspend fun getRandomPokemon(): Pokemon {
        val randomNumber: Int = Random.nextInt(1, 151)
        return pokemonAPIClient.getPokemon(randomNumber)
    }

    suspend fun getPokemonById(id: Int): Pokemon {
        return pokemonAPIClient.getPokemon(id)
    }
}
