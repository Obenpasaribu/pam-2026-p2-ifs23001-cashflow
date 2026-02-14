package org.delcom.services

import org.delcom.data.CashFlowQuery
import org.delcom.entities.CashFlow
import org.delcom.repositories.ICashFlowRepository
import kotlin.time.Instant

class CashFlowService(private val repository: ICashFlowRepository) : ICashFlowService {
    override fun getAllCashFlows(query: CashFlowQuery) = repository.getAll(query)
    override fun getCashFlowById(id: String) = repository.getById(id)
    override fun createCashFlow(type: String, source: String, label: String, amount: Long, description: String) =
        repository.create(type, source, label, amount, description)
    override fun createRawCashFlow(id: String, type: String, source: String, label: String, amount: Long, description: String, createdAt: Instant, updatedAt: Instant) =
        repository.createRaw(id, type, source, label, amount, description, createdAt, updatedAt)
    override fun updateCashFlow(id: String, type: String, source: String, label: String, amount: Long, description: String) =
        repository.update(id, type, source, label, amount, description)
    override fun removeCashFlow(id: String) = repository.delete(id)

    override fun getTypes() = repository.getTypes()
    override fun getSources() = repository.getSources()
    override fun getLabels() = repository.getLabels()
}