package dev.danielwright.main

import org.koin.dsl.module
import org.koin.dsl.single

val pokemonAppModule = module() {
    single<PokemonAPIClient>()
    single<PokemonService>()
    single<PokemonHttpClient>()
    single<ConfigService>()
}
