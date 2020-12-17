package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int> {

    fun getUserByUsername(username: String?): User?
}