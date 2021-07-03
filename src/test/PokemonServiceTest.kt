package dev.danielwright

import dev.danielwright.main.Pokemon
import dev.danielwright.main.PokemonAPIClient
import dev.danielwright.main.PokemonService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class PokemonServiceTest : FunSpec({

    test("my first test") {
        val pokemonApiClient = mockk<PokemonAPIClient>();

        coEvery { pokemonApiClient.getPokemon(any()) } returns Pokemon(1, "test", 0, 0)

        val pokemonService = PokemonService(pokemonApiClient)
        val actual = pokemonService.getRandomPokemon()

        coVerify(exactly = 1) { pokemonApiClient.getPokemon(any()) }

        actual.id shouldBe 1
    }

})