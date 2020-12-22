package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Client
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateClientRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.toClientEntity
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.ClientResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.toClientResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.ClientRepository
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.Role
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.RoleType
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.User
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class ClientService(private val repository: ClientRepository, private val userRepository: UserRepository,
                    private val passwordEncoder: BCryptPasswordEncoder) {

    @Transactional
    fun register(createClientRequest: CreateClientRequest): ClientResponse {
        val client = createClientRequest.toClientEntity(passwordEncoder.encode(createClientRequest.password))
        val savedClient = repository.save(client)
        val user = User(username = savedClient.username, password = savedClient.password,
                roles = mutableSetOf(Role(type = RoleType.CREATOR)))

        userRepository.save(user)
        return savedClient.toClientResponse()
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@userSecurity.hasUserId(#id)")
    fun getById(id: Long): ClientResponse = repository.findByIdOrNull(id)?.toClientResponse()
            ?: throw EntityNotFoundException("Client was not found for the given id: $id")

    @Transactional(readOnly = true)
    fun findAll(): List<ClientResponse> = repository.findAll().map(Client::toClientResponse)
}