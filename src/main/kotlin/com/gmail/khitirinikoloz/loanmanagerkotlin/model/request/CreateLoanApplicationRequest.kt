package com.gmail.khitirinikoloz.loanmanagerkotlin.model.request

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Client
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanApplication
import java.math.BigDecimal
import javax.validation.constraints.NotNull

data class CreateLoanApplicationRequest(
        val amount: BigDecimal,
        @field:NotNull
        val termInMonths: Long?,
        @field:NotNull
        val clientId: Long?
)

fun CreateLoanApplicationRequest.toLoanApplicationEntity(loanOwner: Client) = LoanApplication(
        amount = this.amount,
        termInMonths = checkNotNull(this.termInMonths),
        client = loanOwner
)