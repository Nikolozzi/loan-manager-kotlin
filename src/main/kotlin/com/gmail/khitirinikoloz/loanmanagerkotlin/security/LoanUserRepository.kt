package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoanUserRepository : JpaRepository<LoanUserDetails, Int> {

    fun getLoanUSerDetailsByLoanUsername(username: String?): LoanUserDetails?
}