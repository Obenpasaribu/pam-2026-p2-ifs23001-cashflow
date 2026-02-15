package org.delcom.repositories

import org.delcom.entities.CashFlow

class CashFlowRepository : ICashFlowRepository {
    // Gunakan LinkedHashMap: Pencarian ID instan, tapi urutan data tetap terjaga
    private val cashFlows = LinkedHashMap<String, CashFlow>()

    override fun getAll(): List<CashFlow> = cashFlows.values.toList()

    override fun getById(id: String): CashFlow? = cashFlows[id]

    override fun add(cashFlow: CashFlow) {
        cashFlows[cashFlow.id] = cashFlow
    }

    override fun update(id: String, cashFlow: CashFlow): Boolean {
        if (!cashFlows.containsKey(id)) return false
        cashFlows[id] = cashFlow
        return true
    }

    override fun delete(id: String): Boolean {
        return cashFlows.remove(id) != null
    }

    override fun clearAll() {
        cashFlows.clear()
    }
}