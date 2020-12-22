package com.gmail.khitirinikoloz.loanmanagerkotlin.repository

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Operator
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OperatorRepository : JpaRepository<Operator, Long>