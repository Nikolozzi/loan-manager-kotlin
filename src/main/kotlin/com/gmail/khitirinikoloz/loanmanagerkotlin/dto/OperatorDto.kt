package com.gmail.khitirinikoloz.loanmanagerkotlin.dto

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Operator


data class OperatorDto(
        val id: Long,
        val personalId: String,
        val firstName: String,
        val lastName: String,
        val phoneNumber: String,
        val email: String,
        var password: String
)

fun OperatorDto.toEntity() = Operator(
        id = this.id,
        personalId = this.personalId,
        firstName = this.firstName,
        lastname = this.lastName,
        phoneNumber = this.phoneNumber,
        email = this.email,
        password = this.password
)