package com.gmail.khitirinikoloz.loanmanagerkotlin.repository

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Client
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanApplication
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Operator
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository : JpaRepository<Client, Long>

@Repository
interface LoanApplicationRepository : JpaRepository<LoanApplication, Long> {
    fun findAllByClientId(id: Long): List<LoanApplication>
}

@Repository
interface OperatorRepository : JpaRepository<Operator, Long>