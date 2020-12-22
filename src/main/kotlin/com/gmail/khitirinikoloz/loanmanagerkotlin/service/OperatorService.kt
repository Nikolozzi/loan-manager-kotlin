package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Operator
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateOperatorRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.toOperatorEntity
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.OperatorResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.toOperatorResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.OperatorRepository
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.Role
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.RoleType
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.User
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class OperatorService(private val repository: OperatorRepository, private val userRepository: UserRepository,
                      private val passwordEncoder: BCryptPasswordEncoder) {

    @Transactional
    fun register(createOperatorRequest: CreateOperatorRequest): OperatorResponse {
        val operator = createOperatorRequest.toOperatorEntity(passwordEncoder.encode(createOperatorRequest.password))
        val savedOperator = repository.save(operator)
        val user = User(username = operator.username, password = operator.password,
                roles = mutableSetOf(Role(type = RoleType.CREATOR), Role(type = RoleType.EDITOR)))

        userRepository.save(user)
        return savedOperator.toOperatorResponse()
    }

    @Transactional(readOnly = true)
    fun getById(id: Long) = repository.findByIdOrNull(id)?.toOperatorResponse()
            ?: throw EntityNotFoundException("Operator was not found for given id: $id")


    @Transactional(readOnly = true)
    fun findAll(): List<OperatorResponse> = repository.findAll().map(Operator::toOperatorResponse)
}