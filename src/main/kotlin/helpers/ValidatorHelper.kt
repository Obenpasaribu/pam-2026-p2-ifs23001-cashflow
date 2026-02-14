package org.delcom.helpers

import org.delcom.data.AppException

class ValidatorHelper(private val data: Map<String, Any?>) {
    private val errors = mutableListOf<String>()

    fun addError(field: String, error: String) {
        errors.add("$field: $error")
    }

    fun required(field: String, message: String? = null) {
        val value = data[field]
        if (value == null || (value is String && value.isBlank())) {
            addError(field, message ?: "$field is required")
        }
    }

    fun minAmount(field: String, min: Long, message: String? = null) {
        val value = data[field]
        // Pastikan pengecekan tipe data sesuai (Long atau Int)
        if (value is Long && value < min) {
            addError(field, message ?: "$field must be at least $min")
        } else if (value is Int && value.toLong() < min) {
            addError(field, message ?: "$field must be at least $min")
        }
    }

    fun validate() {
        if (errors.isNotEmpty()) {
            throw AppException(400, errors.joinToString("|"))
        }
    }

}