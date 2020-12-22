package com.gmail.khitirinikoloz.loanmanagerkotlin.model.request

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Client
import java.math.BigDecimal
import java.time.LocalDate

data class CreateClientRequest(
        val personalId: String,
        val firstName: String,
        val lastName: String,
        val username: String,
        val email: String,
        val password: String,
        val birthDate: LocalDate,
        val employer: String,
        val salary: BigDecimal,
        val liability: BigDecimal
)

fun CreateClientRequest.toClientEntity(encodedPassword: String) = Client(
        personalId = this.personalId,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        email = this.email,
        password = encodedPassword,
        birthDate = this.birthDate,
        employer = this.employer,
        salary = this.salary,
        liability = this.liability
)