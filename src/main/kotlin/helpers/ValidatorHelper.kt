package org.delcom.helpers

class ValidatorHelper(private val data: Map<String, Any?>) {
    private val errors = mutableMapOf<String, String>()

    fun required(field: String, message: String) {
        val value = data[field]
        if (value == null || value.toString().trim().isEmpty()) {
            errors[field] = message
        }
    }

    fun minAmount(field: String, min: Long, message: String) {
        val value = data[field]?.toString()?.toLongOrNull()
        if (value == null || value < min) {
            errors[field] = message
        }
    }

    // TAMBAHKAN DUA FUNGSI INI:
    fun hasErrors(): Boolean = errors.isNotEmpty()

    fun getErrors(): Map<String, String> = errors

    // Fungsi validate() yang lama (opsional jika ingin tetap dipakai)
    fun validate() {
        if (hasErrors()) {
            // Kita biarkan Controller yang menangani responnya agar sesuai test
        }
    }
}