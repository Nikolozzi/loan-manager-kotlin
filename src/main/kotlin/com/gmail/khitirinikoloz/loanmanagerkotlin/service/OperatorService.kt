package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.OperatorDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.toEntity
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.toDto
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
    fun register(operatorDto: OperatorDto): OperatorDto {
        operatorDto.password = passwordEncoder.encode(operatorDto.password)
        val operator = repository.save(operatorDto.toEntity())
        val user = User(username = operator.username, password = operator.password,
                roles = mutableSetOf(Role(type = RoleType.CREATOR), Role(type = RoleType.EDITOR)))

        userRepository.save(user)
        return operator.toDto()
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): OperatorDto = repository.findByIdOrNull(id)?.toDto()
            ?: throw EntityNotFoundException("Operator was not found for given id: $id")


    @Transactional(readOnly = true)
    fun getAll(): List<OperatorDto> = repository.findAll().map { it.toDto() }
}