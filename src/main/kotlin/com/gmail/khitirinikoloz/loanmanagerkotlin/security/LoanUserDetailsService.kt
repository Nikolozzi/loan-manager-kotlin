package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class LoanUserDetailsService : UserDetailsService {
    @Autowired
    private lateinit var repository: UserRepository

    override fun loadUserByUsername(username: String?): UserDetails {
        val user = repository.getUserByUsername(username)
                ?: throw UsernameNotFoundException("User does not exist")

        return LoanUserDetails(user)
    }
}