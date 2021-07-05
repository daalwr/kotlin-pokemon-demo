package dev.danielwright.main

import io.kotest.core.spec.style.FunSpec
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

class ModuleTest : FunSpec(), KoinTest {

    init {
        test("check modules") {
            checkModules {
                modules(pokemonAppModule)
            }
        }
    }
}
