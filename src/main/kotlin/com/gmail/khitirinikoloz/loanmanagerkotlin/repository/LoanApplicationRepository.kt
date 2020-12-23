package com.gmail.khitirinikoloz.loanmanagerkotlin.repository

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoanApplicationRepository : JpaRepository<LoanApplication, Long> {
    fun findAllByClientId(id: Long): List<LoanApplication>
}