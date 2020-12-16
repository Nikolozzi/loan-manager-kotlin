package com.gmail.khitirinikoloz.loanmanagerkotlin.model

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.OperatorDto
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Operator(
        @Id @GeneratedValue var id: Long,
        @Column(unique = true, length = 11) var personalId: String,
        var firstName: String,
        var lastname: String,
        var phoneNumber: String,
        @Column(unique = true) var email: String,
        var password: String
)

fun Operator.toDto() = OperatorDto(
        id = this.id,
        personalId = this.personalId,
        firstName = this.firstName,
        lastName = this.lastname,
        phoneNumber = this.phoneNumber,
        email = this.email,
        password = this.password
)