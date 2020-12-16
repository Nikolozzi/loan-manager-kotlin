package com.gmail.khitirinikoloz.loanmanagerkotlin.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.LoanApplicationDto
import javax.persistence.*

@Entity
class LoanApplication(
        @Id @GeneratedValue var id: Long,
        var amount: Double,
        var term: Long, //months
        var status: LoanStatus?,
        var score: Double?,
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "client_id", nullable = false)
        var client: Client // ???
)

fun LoanApplication.toDto() = LoanApplicationDto(
        id = this.id,
        amount = this.amount,
        term = this.term,
        status = this.status,
        score = this.score,
        client = this.client.toDto()
)


enum class LoanStatus { APPROVED, REJECTED, MANUAL }