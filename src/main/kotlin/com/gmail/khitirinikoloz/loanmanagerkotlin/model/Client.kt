package com.gmail.khitirinikoloz.loanmanagerkotlin.model

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.ClientResponse
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*

@Entity
class Client(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @Column(unique = true, length = 11)
        val personalId: String,
        val firstName: String,
        val lastName: String,
        @Column(unique = true)
        val username: String,
        @Column(unique = true)
        val email: String,
        val password: String,
        val birthDate: LocalDate,
        val employer: String,
        val salary: BigDecimal,
        val liability: BigDecimal,
        @OneToMany(mappedBy = "client", orphanRemoval = true, cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val loans: MutableList<LoanApplication> = mutableListOf()
)

fun Client.toClientResponse() = ClientResponse(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        email = this.email,
        birthDate = this.birthDate,
        employer = this.employer,
        salary = this.salary,
        liability = this.liability,
)