package org.delcom

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.controllers.CashFlowController
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val cashFlowController: CashFlowController by inject()

    // Pindahkan install StatusPages ke sini agar tidak duplikat
    install(StatusPages) {
        exception<AppException> { call, cause ->
            val statusCode = HttpStatusCode.fromValue(cause.code)
            val statusLabel = if (cause.code in 400..499) "fail" else "error"
            call.respond(statusCode, DataResponse<Any?>(statusLabel, cause.message, null))
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                DataResponse<Any?>("error", cause.message ?: "Terjadi kesalahan internal", null)
            )
        }
    }

    routing {
        get("/") { call.respondText("CashFlow API is running!") }

        route("/cash-flows") {
            post("/setup") { cashFlowController.setupData(call) }

            // Extends (Harus diletakkan di atas route "/{id}" agar tidak dikenali sebagai parameter id)
            get("/types") { cashFlowController.getTypes(call) }
            get("/sources") { cashFlowController.getSources(call) }
            get("/labels") { cashFlowController.getLabels(call) }

            // CRUD
            get { cashFlowController.getAllCashFlows(call) }
            post { cashFlowController.createCashFlow(call) }
            get("/{id}") { cashFlowController.getCashFlowById(call) }
            put("/{id}") { cashFlowController.updateCashFlow(call) }
            delete("/{id}") { cashFlowController.deleteCashFlow(call) }
        }
    }
}