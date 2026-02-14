package org.delcom.controllers

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.delcom.data.*
import org.delcom.helpers.ValidatorHelper
import org.delcom.helpers.loadInitialData
import org.delcom.services.ICashFlowService
import kotlin.time.*

class CashFlowController(private val cashFlowService: ICashFlowService) {

    suspend fun setupData(call: ApplicationCall) {
        val query = CashFlowQuery()
        val cashFlows = cashFlowService.getAllCashFlows(query)
        for (cashFlow in cashFlows) {
            cashFlowService.removeCashFlow(cashFlow.id)
        }

        val initCashFlows = loadInitialData()
        for (cashFlow in initCashFlows) {
            cashFlowService.createRawCashFlow(
                cashFlow.id, cashFlow.type, cashFlow.source, cashFlow.label,
                cashFlow.amount, cashFlow.description, cashFlow.createdAt, cashFlow.updatedAt
            )
        }

        call.respond(DataResponse<Any?>("success", "Berhasil memuat data awal", null))
    }

    suspend fun getAllCashFlows(call: ApplicationCall) {
        val queryParams = call.request.queryParameters
        val query = CashFlowQuery(
            type = queryParams["type"],
            source = queryParams["source"],
            labels = queryParams["labels"],
            gteAmount = queryParams["gteAmount"]?.toLongOrNull(),
            lteAmount = queryParams["lteAmount"]?.toLongOrNull(),
            search = queryParams["search"],
            startDate = queryParams["startDate"],
            endDate = queryParams["endDate"]
        )

        val cashFlows = cashFlowService.getAllCashFlows(query)
        // Test meminta field "total" dan "cashFlows"
        call.respond(DataResponse("success", "Berhasil mengambil daftar catatan keuangan", mapOf(
            "cashFlows" to cashFlows,
            "total" to cashFlows.size
        )))
    }

    suspend fun getCashFlowById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong!")
        val cashFlow = cashFlowService.getCashFlowById(id)
            ?: throw AppException(404, "Data catatan keuangan tidak tersedia!")

        call.respond(DataResponse("success", "Berhasil mengambil data catatan keuangan", mapOf("cashFlow" to cashFlow)))
    }

    suspend fun createCashFlow(call: ApplicationCall) {
        val request = try { call.receive<CashFlowRequest>() } catch (e: Exception) { CashFlowRequest() }

        val requestData = mapOf(
            "type" to request.type, "source" to request.source,
            "label" to request.label, "amount" to request.amount, "description" to request.description
        )

        val validatorHelper = ValidatorHelper(requestData).apply {
            required("type", "Tipe tidak boleh kosong")
            required("source", "Sumber tidak boleh kosong")
            required("label", "Label tidak boleh kosong") // Test meminta label divalidasi
            required("amount", "Jumlah tidak boleh kosong")
            required("description", "Deskripsi tidak boleh kosong")
            minAmount("amount", 1, "Jumlah harus lebih besar dari 0")
        }

        if (validatorHelper.hasErrors()) {
            // Test meminta status "fail" dan pesan khusus ini
            call.respond(io.ktor.http.HttpStatusCode.BadRequest, DataResponse("fail", "Data yang dikirimkan tidak valid!", validatorHelper.getErrors()))
            return
        }

        val newId = cashFlowService.createCashFlow(
            request.type!!, request.source!!, request.label!!, request.amount!!, request.description!!
        )

        call.respond(DataResponse("success", "Berhasil menambahkan data catatan keuangan", CashFlowResponse(newId)))
    }

    suspend fun updateCashFlow(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong!")
        val request = try { call.receive<CashFlowRequest>() } catch (e: Exception) { CashFlowRequest() }

        val requestData = mapOf(
            "type" to request.type, "source" to request.source,
            "label" to request.label, "amount" to request.amount, "description" to request.description
        )

        val validatorHelper = ValidatorHelper(requestData).apply {
            required("type", "Tipe tidak boleh kosong")
            required("source", "Sumber tidak boleh kosong")
            required("label", "Label tidak boleh kosong")
            required("amount", "Jumlah tidak boleh kosong")
            required("description", "Deskripsi tidak boleh kosong")
        }

        if (validatorHelper.hasErrors()) {
            call.respond(io.ktor.http.HttpStatusCode.BadRequest, DataResponse("fail", "Data yang dikirimkan tidak valid!", validatorHelper.getErrors()))
            return
        }

        val isUpdated = cashFlowService.updateCashFlow(
            id, request.type!!, request.source!!, request.label!!, request.amount!!, request.description!!
        )
        if (!isUpdated) throw AppException(404, "Data catatan keuangan tidak tersedia!")

        call.respond(DataResponse("success", "Berhasil mengubah data catatan keuangan", null))
    }

    suspend fun deleteCashFlow(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID tidak boleh kosong!")
        if (!cashFlowService.removeCashFlow(id)) throw AppException(404, "Data catatan keuangan tidak tersedia!")

        call.respond(DataResponse("success", "Berhasil menghapus data catatan keuangan", null))
    }

    suspend fun getTypes(call: ApplicationCall) {
        val types = cashFlowService.getTypes()
        call.respond(DataResponse("success", "Berhasil mengambil daftar tipe catatan keuangan", mapOf("types" to types)))
    }

    suspend fun getSources(call: ApplicationCall) {
        val sources = cashFlowService.getSources()
        call.respond(DataResponse("success", "Berhasil mengambil daftar source catatan keuangan", mapOf("sources" to sources)))
    }

    suspend fun getLabels(call: ApplicationCall) {
        val labels = cashFlowService.getLabels()
        call.respond(DataResponse("success", "Berhasil mengambil daftar label catatan keuangan", mapOf("labels" to labels)))
    }
}