package org.delcom.services

import org.delcom.data.CashFlowQuery
import org.delcom.data.CashFlowRequest
import org.delcom.entities.CashFlow
import org.delcom.helpers.loadInitialData
import org.delcom.repositories.ICashFlowRepository
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class CashFlowService(private val repository: ICashFlowRepository) : ICashFlowService {

    // ... (getAllCashFlows tetap sama) ...
    override fun getAllCashFlows(query: CashFlowQuery): List<CashFlow> {
        return repository.getAll().filter { cf ->
            val matchType = query.type?.let { cf.type.equals(it, true) } ?: true
            val matchSource = query.source?.let { cf.source.equals(it, true) } ?: true
            val matchGte = query.gteAmount?.let { cf.amount >= it } ?: true
            val matchLte = query.lteAmount?.let { cf.amount <= it } ?: true
            val matchSearch = query.search?.let { cf.description.contains(it, true) } ?: true

            val matchLabels = query.labels?.let { param ->
                val searchTags = param.split(",").map { it.trim() }.filter { it.isNotBlank() }
                if (searchTags.isEmpty()) true
                else {
                    val itemLabels = cf.label.split(",").map { it.trim() }
                    searchTags.any { tag -> itemLabels.any { it.equals(tag, ignoreCase = true) } }
                }
            } ?: true

            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val cfDate = try { LocalDate.parse(cf.createdAt.substring(0, 10)) } catch (e: Exception) { null }
            val matchStart = query.startDate?.let { val start = LocalDate.parse(it, dateFormatter); cfDate?.let { d -> !d.isBefore(start) } ?: false } ?: true
            val matchEnd = query.endDate?.let { val end = LocalDate.parse(it, dateFormatter); cfDate?.let { d -> !d.isAfter(end) } ?: false } ?: true

            matchType && matchSource && matchLabels && matchGte && matchLte && matchSearch && matchStart && matchEnd
        }
    }

    override fun getCashFlowById(id: String) = repository.getById(id)

    // Implementasi Method Baru
    override fun createCashFlowRaw(type: String, source: String, label: String, amount: Double, description: String): String {
        val id = UUID.randomUUID().toString()
        val now = OffsetDateTime.now().toString()
        val newCf = CashFlow(id, type, source, label, amount, description, now, now)
        repository.add(newCf)
        return id
    }

    // Implementasi untuk backward compatibility (jika diperlukan)
    override fun createCashFlow(req: CashFlowRequest): String {
        return createCashFlowRaw(req.type!!, req.source!!, req.label!!, req.amount!!.toDouble(), req.description!!)
    }

    override fun updateCashFlowRaw(id: String, type: String, source: String, label: String, amount: Double, description: String): Boolean {
        val existing = repository.getById(id) ?: return false
        val updated = existing.copy(
            type = type, source = source, label = label, amount = amount, description = description,
            updatedAt = OffsetDateTime.now().toString()
        )
        return repository.update(id, updated)
    }

    override fun deleteCashFlow(id: String) = repository.delete(id)

    override fun setupInitialData(): Int {
        repository.clearAll()
        val data = loadInitialData()
        data.forEach { repository.add(it) }
        return data.size
    }

    override fun getDistinctTypes() = repository.getAll().map { it.type }.distinct()
    override fun getDistinctSources() = repository.getAll().map { it.source }.distinct()
    override fun getDistinctLabels() = repository.getAll().flatMap { it.label.split(",") }.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
}