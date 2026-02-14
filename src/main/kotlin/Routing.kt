package org.delcom

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.delcom.controllers.CashFlowController
import org.delcom.data.AppException
import org.delcom.data.ErrorResponse
import org.delcom.helpers.parseMessageToMap
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val cashFlowController: CashFlowController by inject()

    install(StatusPages) {
        exception<AppException> { call, cause ->
            val dataMap = parseMessageToMap(cause.message)
            call.respond(
                status = HttpStatusCode.fromValue(cause.code),
                message = ErrorResponse(
                    status = "fail",
                    message = if (dataMap.isEmpty()) cause.message else "Data tidak valid!",
                    data = dataMap
                )
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse("error", cause.message ?: "Unknown error")
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