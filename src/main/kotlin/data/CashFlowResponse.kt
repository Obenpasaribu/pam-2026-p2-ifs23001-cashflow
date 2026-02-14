package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.CashFlow

@Serializable
data class CashFlowResponse(
    val cashFlowId: String
)