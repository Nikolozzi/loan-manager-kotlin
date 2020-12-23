package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class LoanUserDetailsService : UserDetailsService {
    @Autowired
    private lateinit var repository: LoanUserRepository

    override fun loadUserByUsername(username: String?) = repository.getLoanUSerDetailsByLoanUsername(username)
            ?: throw UsernameNotFoundException("User does not exist")
}