package com.gmail.khitirinikoloz.loanmanagerkotlin.model.response

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanStatus
import java.math.BigDecimal

data class LoanApplicationResponse (
        val id: Long,
        val amount: BigDecimal,
        val termInMonths: Long,
        val score: BigDecimal,
        val status: LoanStatus,
        val client: ClientResponse
)