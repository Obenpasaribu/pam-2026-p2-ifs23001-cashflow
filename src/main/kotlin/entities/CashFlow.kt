package org.delcom.entities

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class CashFlow(
    val id: String = UUID.randomUUID().toString(),
    var type: String,
    var source: String,
    var label: String,
    var amount: Long,
    var description: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)