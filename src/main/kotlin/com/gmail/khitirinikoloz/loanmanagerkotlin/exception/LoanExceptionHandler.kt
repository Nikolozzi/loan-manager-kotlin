package com.gmail.khitirinikoloz.loanmanagerkotlin.exception

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AuthorizationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter
import javax.persistence.EntityNotFoundException
import javax.persistence.NoResultException

@ControllerAdvice
class LoanExceptionHandler {

    @ExceptionHandler(
            EntityNotFoundException::class,
            NoSuchElementException::class,
            NoResultException::class,
            EmptyResultDataAccessException::class
    )
    fun entityNotFoundException(e: Exception): ResponseEntity<ErrorResponse> =
            buildErrorResponse(HttpStatus.NOT_FOUND, "Resource not found", e)

    @ExceptionHandler(
            ConstraintViolationException::class,
            IllegalArgumentException::class,
            MethodArgumentNotValidException::class
    )
    fun constraintViolationException(e: Exception): ResponseEntity<ErrorResponse> =
            buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad request", e)

    //Authorization handlers
    @ExceptionHandler(AuthorizationServiceException::class)
    fun unauthorizedException(e: Exception): ResponseEntity<ErrorResponse> =
            buildErrorResponse(HttpStatus.FORBIDDEN, "You are not authorized to do this operation", e)

    @ExceptionHandler(AuthenticationException::class)
    fun forbiddenException(e: Exception): ResponseEntity<ErrorResponse> =
            buildErrorResponse(HttpStatus.UNAUTHORIZED, "You are not allowed to do this operation", e)

    private fun buildErrorResponse(status: HttpStatus, message: String, e: Exception)
            : ResponseEntity<ErrorResponse> {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        val stackTrace = sw.toString()

        return ResponseEntity(ErrorResponse(status, message, stackTrace), status)
    }
}