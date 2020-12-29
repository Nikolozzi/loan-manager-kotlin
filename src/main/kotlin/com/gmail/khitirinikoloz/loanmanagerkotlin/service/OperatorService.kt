package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Operator
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateOperatorRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.toOperatorEntity
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.OperatorResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.toOperatorResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.OperatorRepository
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.LoanUserDetails
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.LoanUserRepository
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.Role
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.RoleType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class OperatorService(private val repository: OperatorRepository, private val loanUserRepository: LoanUserRepository,
                      private val passwordEncoder: BCryptPasswordEncoder) {

    @Transactional
    fun register(createOperatorRequest: CreateOperatorRequest): OperatorResponse {
        val operator = createOperatorRequest.toOperatorEntity(passwordEncoder.encode(createOperatorRequest.password))
        val savedOperator = repository.save(operator)
        val user = LoanUserDetails(loanUsername = operator.username, loanUserPassword = operator.password,
                roles = mutableSetOf(Role(type = RoleType.CREATOR), Role(type = RoleType.EDITOR)))

        loanUserRepository.save(user)
        return savedOperator.toOperatorResponse()
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('EDITOR')")
    fun getById(id: Long) = repository.findByIdOrNull(id)?.toOperatorResponse()
            ?: throw EntityNotFoundException("Operator was not found for given id: $id")

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('EDITOR')")
    fun findAll(): List<OperatorResponse> = repository.findAll().map(Operator::toOperatorResponse)
}