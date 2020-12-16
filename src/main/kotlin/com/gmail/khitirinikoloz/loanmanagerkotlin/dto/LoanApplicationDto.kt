package com.gmail.khitirinikoloz.loanmanagerkotlin.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanApplication
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoanApplicationDto(
        var id: Long,
        val amount: Double,
        val term: Long,
        val score: Double?,
        val status: LoanStatus?,
        var client: ClientDto? = null
) {
    companion object {
        fun fromLoanApplication(loanApplication: LoanApplication) =
                LoanApplicationDto(loanApplication.id, loanApplication.amount, loanApplication.term,
                        loanApplication.score, loanApplication.status)
    }
}

fun LoanApplicationDto.toEntity() = LoanApplication(
        id = this.id,
        amount = this.amount,
        term = this.term,
        status = this.status,
        score = this.score,
        client = client!!.toEntity()
)
