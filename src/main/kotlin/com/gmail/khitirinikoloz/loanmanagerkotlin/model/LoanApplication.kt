package com.gmail.khitirinikoloz.loanmanagerkotlin.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.LoanApplicationResponse
import java.math.BigDecimal
import javax.persistence.*

@Entity
class LoanApplication(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val amount: BigDecimal,
        @Column(name = "term")
        val termInMonths: Long,
        var status: LoanStatus? = null,
        var score: BigDecimal? = null,
        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "client_id", nullable = false)
        val client: Client
)

fun LoanApplication.toLoanApplicationResponse() = LoanApplicationResponse(
        id = this.id,
        amount = this.amount,
        termInMonths = this.termInMonths,
        score = checkNotNull(score),
        status = checkNotNull(this.status),
        client = this.client.toClientResponse()
)


enum class LoanStatus { APPROVED, REJECTED, MANUAL }