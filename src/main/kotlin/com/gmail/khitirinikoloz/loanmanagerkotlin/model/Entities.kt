package com.gmail.khitirinikoloz.loanmanagerkotlin.model

import java.time.LocalDate
import javax.persistence.*

@Entity
class Operator(
        @Id @GeneratedValue private var id: Long,
        @Column(unique = true, length = 11) var personalId: String,
        var firstName: String,
        var lastname: String,
        var phoneNumber: String,
        @Column(unique = true) var email: String,
        var password: String
)


@Entity
class Client(
        @Id @GeneratedValue private var id: Long,
        @Column(unique = true, length = 11) var personalId: String,
        var firstName: String,
        var lastName: String,
        @Column(unique = true) var email: String,
        var password: String,
        var birthDate: LocalDate,
        var salary: Double,
        var liability: Double,
        @OneToMany(mappedBy = "client", fetch = FetchType.LAZY) var loans: MutableList<LoanApplication>
)

@Entity
class LoanApplication(
        @Id @GeneratedValue var id: Long,
        var amount: Double,
        var term: Long,
        @ManyToOne @JoinColumn(name = "client_id") var client: Client
)