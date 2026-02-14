package org.delcom.repositories

import org.delcom.data.CashFlowQuery
import org.delcom.entities.CashFlow
import kotlin.time.Instant


interface ICashFlowRepository {
    fun getAll(query: CashFlowQuery): List<CashFlow>
    fun getById(id: String): CashFlow?
    fun create(type: String, source: String, label: String, amount: Long, description: String): String
    fun createRaw(id: String, type: String, source: String, label: String, amount: Long, description: String, createdAt: Instant, updatedAt: Instant)
    fun update(id: String, type: String, source: String, label: String, amount: Long, description: String): Boolean
    fun delete(id: String): Boolean
    fun getTypes(): List<String>
    fun getSources(): List<String>
    fun getLabels(): List<String>
}