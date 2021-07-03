package dev.danielwright.main

import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(val id: Int, val name: String, val height: Int, val weight: Int)
