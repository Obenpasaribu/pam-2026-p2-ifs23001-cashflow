package org.delcom.data

import org.delcom.controllers.CashFlowController
import org.delcom.repositories.CashFlowRepository
import org.delcom.repositories.ICashFlowRepository
import org.delcom.services.CashFlowService
import org.delcom.services.ICashFlowService
import org.koin.dsl.module

val appModule = module {
    single<ICashFlowRepository> { CashFlowRepository() }
    single<ICashFlowService> { CashFlowService(get()) }
    single { CashFlowController(get()) }
}