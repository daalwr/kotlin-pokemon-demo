package dev.danielwright.main

import com.sksamuel.hoplite.ConfigLoader

class ConfigService {

    fun getConfig(): Config {
        return ConfigLoader().loadConfigOrThrow("/application.conf")
    }
}
