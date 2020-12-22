package com.gmail.khitirinikoloz.loanmanagerkotlin.model.response

import java.math.BigDecimal
import java.time.LocalDate

data class ClientResponse(
        val id: Long,
        val firstName: String,
        val lastName: String,
        val username: String,
        val email: String,
        val birthDate: LocalDate,
        val employer: String,
        val salary: BigDecimal,
        val liability: BigDecimal,
)