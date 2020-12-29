package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Client
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateClientRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.toClientEntity
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.ClientResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.toClientResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.ClientRepository
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
class ClientService(private val repository: ClientRepository, private val loanUserRepository: LoanUserRepository,
                    private val passwordEncoder: BCryptPasswordEncoder) {

    @Transactional
    fun register(createClientRequest: CreateClientRequest): ClientResponse {
        val client = createClientRequest.toClientEntity(passwordEncoder.encode(createClientRequest.password))
        val savedClient = repository.save(client)
        val user = LoanUserDetails(clientId = savedClient.id, loanUsername = savedClient.username,
                loanUserPassword = savedClient.password, roles = mutableSetOf(Role(type = RoleType.CREATOR)))

        loanUserRepository.save(user)
        return savedClient.toClientResponse()
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('EDITOR') OR (hasAuthority('CREATOR') AND @userSecurity.hasUserId(#id))")
    fun getById(id: Long): ClientResponse = repository.findByIdOrNull(id)?.toClientResponse()
            ?: throw EntityNotFoundException("Client was not found for the given id: $id")

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('EDITOR')")
    fun findAll(): List<ClientResponse> = repository.findAll().map(Client::toClientResponse)
}