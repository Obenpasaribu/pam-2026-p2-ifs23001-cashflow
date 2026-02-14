package org.delcom.services

import org.delcom.data.CashFlowQuery
import org.delcom.entities.CashFlow
import kotlin.time.Instant

interface ICashFlowService {
    fun getAllCashFlows(query: CashFlowQuery): List<CashFlow>
    fun getCashFlowById(id: String): CashFlow?
    fun createCashFlow(type: String, source: String, label: String, amount: Long, description: String): String
    fun createRawCashFlow(id: String, type: String, source: String, label: String, amount: Long, description: String, createdAt: Instant, updatedAt: Instant)
    fun updateCashFlow(id: String, type: String, source: String, label: String, amount: Long, description: String): Boolean
    fun removeCashFlow(id: String): Boolean

    fun getTypes(): List<String>
    fun getSources(): List<String>
    fun getLabels(): List<String>
}