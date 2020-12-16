package com.gmail.khitirinikoloz.loanmanagerkotlin.exception

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import java.util.*

class ErrorResponse(status: HttpStatus, val message: String, val stackTrace: String) {
    val code = status.value()
    val status = status.name

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    val timestamp: Date = Date()
}