package org.delcom

import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.* // WAJIB ADA
import io.ktor.server.response.* // WAJIB ADA
import kotlinx.serialization.json.Json
import org.delcom.data.* // Memastikan AppException & DataResponse terbaca
import org.delcom.data.appModule
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    val dotenv = dotenv {
        directory = "."
        ignoreIfMissing = true
    }
    dotenv.entries().forEach { System.setProperty(it.key, it.value) }

    EngineMain.main(args)
}

fun Application.module() {
    install(CORS) { anyHost() }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }

    install(Koin) { modules(appModule) }

    configureRouting()
}