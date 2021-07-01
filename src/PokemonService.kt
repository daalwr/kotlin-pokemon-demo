package dev.danielwright

import kotlin.random.Random

class PokemonService(val pokemonAPIClient: PokemonAPIClient) {

    suspend fun getRandomPokemon(): Pokemon {
        val randomNumber: Int = Random.nextInt(1, 151)
        return pokemonAPIClient.getPokemon(randomNumber)
    }
}