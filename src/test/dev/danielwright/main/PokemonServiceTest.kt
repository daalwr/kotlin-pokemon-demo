package dev.danielwright.main

import dev.danielwright.main.model.Pokemon
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class PokemonServiceTest : FunSpec({

    test("PokemonService returns a Pokemon from the Pokemon API Client") {
        val pokemonApiClient = mockk<PokemonAPIClient>()

        coEvery { pokemonApiClient.getPokemon(any()) } returns Pokemon(1, "test", 0, 0)

        val pokemonService = PokemonService(pokemonApiClient)
        val actual = pokemonService.getRandomPokemon()

        coVerify(exactly = 1) { pokemonApiClient.getPokemon(any()) }

        actual.id shouldBe 1
    }
})
