package com.gmail.khitirinikoloz.loanmanagerkotlin.model

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.ClientDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.LoanApplicationDto
import java.time.LocalDate
import javax.persistence.*

@Entity
class Client(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long,
        @Column(unique = true, length = 11) var personalId: String,
        var firstName: String,
        var lastName: String,
        @Column(unique = true)
        var username: String,
        @Column(unique = true) var email: String,
        var password: String,
        var birthDate: LocalDate,
        var employer: String,
        var salary: Double,
        var liability: Double,
        @OneToMany(mappedBy = "client", orphanRemoval = true, cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var loans: MutableList<LoanApplication> = mutableListOf()
)

fun Client.toDto() = ClientDto(
        id = this.id,
        personalId = this.personalId,
        firstName = this.firstName,
        lastName = this.lastName,
        username = this.username,
        email = this.email,
        password = this.password,
        birthDate = this.birthDate,
        employer = this.employer,
        salary = this.salary,
        liability = this.liability,
        loans = this.loans.map { LoanApplicationDto.fromLoanApplication(it) }.toMutableList()
)