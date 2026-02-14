package org.delcom.helpers

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.delcom.entities.CashFlow
import java.io.File

@Serializable
data class CashFlowsContainer(
    val cashFlows: List<CashFlow>
)

fun loadInitialData(): List<CashFlow> {
    return try {
        val jsonFile = File("data-awal.json")

        if (!jsonFile.exists()) {
            val resource = object {}.javaClass.classLoader.getResource("data-awal.json")
                ?: throw IllegalStateException("File data-awal.json tidak ditemukan")

            val jsonText = resource.readText()
            Json.decodeFromString<CashFlowsContainer>(jsonText).cashFlows
        } else {
            val jsonText = jsonFile.readText()
            Json.decodeFromString<CashFlowsContainer>(jsonText).cashFlows
        }
    } catch (e: Exception) {
        println("Error loading JSON data: ${e.message}")
        emptyList()
    }
}