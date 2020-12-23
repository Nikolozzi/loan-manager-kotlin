package com.gmail.khitirinikoloz.loanmanagerkotlin.model.response

data class OperatorResponse(
        val id: Long,
        val firstName: String,
        val lastName: String,
        var username: String,
        val phoneNumber: String,
        val email: String,
)