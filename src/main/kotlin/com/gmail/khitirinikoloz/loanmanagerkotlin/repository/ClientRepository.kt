package com.gmail.khitirinikoloz.loanmanagerkotlin.repository

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository : JpaRepository<Client, Long>