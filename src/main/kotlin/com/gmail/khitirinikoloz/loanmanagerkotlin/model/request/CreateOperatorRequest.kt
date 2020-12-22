package com.gmail.khitirinikoloz.loanmanagerkotlin.model.request

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Operator

data class CreateOperatorRequest(
        val personalId: String,
        val firstName: String,
        val lastName: String,
        val username: String,
        val phoneNumber: String,
        val email: String,
        val password: String
)

fun CreateOperatorRequest.toOperatorEntity(encodedPassword: String) = Operator(
        personalId = this.personalId,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        phoneNumber = this.phoneNumber,
        email = this.email,
        password = encodedPassword
)