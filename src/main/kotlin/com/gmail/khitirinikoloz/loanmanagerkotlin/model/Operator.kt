package com.gmail.khitirinikoloz.loanmanagerkotlin.model

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.OperatorDto
import javax.persistence.*

@Entity
class Operator(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long,
        @Column(unique = true, length = 11) var personalId: String,
        var firstName: String,
        var lastName: String,
        var username: String,
        var phoneNumber: String,
        @Column(unique = true) var email: String,
        var password: String
)

fun Operator.toDto() = OperatorDto(
        id = this.id,
        personalId = this.personalId,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        phoneNumber = this.phoneNumber,
        email = this.email,
        password = this.password
)