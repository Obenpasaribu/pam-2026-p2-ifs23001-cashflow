package org.delcom.data

data class CashFlowQuery(
    val type: String? = null,
    val source: String? = null,
    val labels: String? = null,
    val gteAmount: Long? = null,
    val lteAmount: Long? = null,
    val search: String? = null,
    val startDate: String? = null,
    val endDate: String? = null
)