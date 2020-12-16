package com.gmail.khitirinikoloz.loanmanagerkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class LoanManagerKotlinApplication

fun main(args: Array<String>) {
    runApplication<LoanManagerKotlinApplication>(*args)
}
