package org.delcom.repositories

import org.delcom.data.CashFlowQuery
import org.delcom.entities.CashFlow
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Instant


class CashFlowRepository : ICashFlowRepository {
    private val data = mutableListOf<CashFlow>()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun getAll(query: CashFlowQuery): List<CashFlow> {
        var filtered = data.toList()

        // Filter Type & Source
        query.type?.let { q -> filtered = filtered.filter { it.type.equals(q, true) } }
        query.source?.let { q -> filtered = filtered.filter { it.source.equals(q, true) } }

        // Filter Labels
        query.labels?.let { q ->
            val queryLabels = q.split(",").map { it.trim().lowercase() }
            filtered = filtered.filter { item ->
                val itemLabels = item.label.split(",").map { it.trim().lowercase() }
                queryLabels.any { it in itemLabels }
            }
        }

        // Filter Amount
        query.gteAmount?.let { q -> filtered = filtered.filter { it.amount >= q } }
        query.lteAmount?.let { q -> filtered = filtered.filter { it.amount <= q } }
        query.search?.let { q -> filtered = filtered.filter { it.description.contains(q, true) } }

        // Filter Tanggal (Konversi kotlin.time.Instant ke java.time.LocalDate)
        query.startDate?.let { q ->
            val start = LocalDate.parse(q, dateFormatter)
            filtered = filtered.filter {
                val itemDate = java.time.Instant.ofEpochMilli(it.createdAt.toEpochMilliseconds())
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                !itemDate.isBefore(start)
            }
        }

        query.endDate?.let { q ->
            val end = LocalDate.parse(q, dateFormatter)
            filtered = filtered.filter {
                val itemDate = java.time.Instant.ofEpochMilli(it.createdAt.toEpochMilliseconds())
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                !itemDate.isAfter(end)
            }
        }

        return filtered
    }

    override fun getById(id: String): CashFlow? = data.find { it.id == id }

    override fun create(type: String, source: String, label: String, amount: Long, description: String): String {
        val newObj = CashFlow(type = type, source = source, label = label, amount = amount, description = description)
        data.add(newObj)
        return newObj.id
    }

    override fun createRaw(id: String, type: String, source: String, label: String, amount: Long, description: String, createdAt: Instant, updatedAt: Instant) {
        data.add(CashFlow(id, type, source, label, amount, description, createdAt, updatedAt))
    }

    override fun update(id: String, type: String, source: String, label: String, amount: Long, description: String): Boolean {
        val target = getById(id) ?: return false
        target.type = type
        target.source = source
        target.label = label
        target.amount = amount
        target.description = description
        target.updatedAt = Clock.System.now()
        return true
    }

    override fun delete(id: String): Boolean {
        val target = getById(id) ?: return false
        data.remove(target)
        return true
    }

    override fun getTypes(): List<String> = data.map { it.type }.distinct()
    override fun getSources(): List<String> = data.map { it.source }.distinct()
    override fun getLabels(): List<String> = data.flatMap { it.label.split(",") }.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
}