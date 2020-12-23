package com.gmail.khitirinikoloz.loanmanagerkotlin.model

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.OperatorResponse
import javax.persistence.*

@Entity
class Operator(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @Column(unique = true, length = 11)
        val personalId: String,
        val firstName: String,
        val lastName: String,
        val username: String,
        val phoneNumber: String,
        @Column(unique = true)
        val email: String,
        val password: String
)

fun Operator.toOperatorResponse() = OperatorResponse(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        phoneNumber = this.phoneNumber,
        email = this.email,
)