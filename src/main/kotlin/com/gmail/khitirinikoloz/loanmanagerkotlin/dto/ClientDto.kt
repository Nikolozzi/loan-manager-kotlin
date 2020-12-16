package com.gmail.khitirinikoloz.loanmanagerkotlin.dto

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Client
import java.time.LocalDate
import javax.validation.constraints.NotNull

data class ClientDto(
        val id: Long,
        val personalId: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        var password: String,
        val birthDate: LocalDate,
        val employer: String,
        @field:NotNull
        val salary: Double?,
        @field:NotNull
        val liability: Double?,
        val loans: MutableList<LoanApplicationDto>? = mutableListOf()
)

fun ClientDto.toEntity() = Client(
        id = this.id,
        personalId = this.personalId,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        password = this.password,
        birthDate = this.birthDate,
        employer = this.employer,
        salary = this.salary!!,
        liability = this.liability!!
)