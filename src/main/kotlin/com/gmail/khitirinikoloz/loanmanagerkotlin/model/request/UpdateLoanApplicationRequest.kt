package com.gmail.khitirinikoloz.loanmanagerkotlin.model.request

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanStatus
import java.math.BigDecimal

data class UpdateLoanApplicationRequest(
        val status: LoanStatus?,
        val score: BigDecimal?,
)