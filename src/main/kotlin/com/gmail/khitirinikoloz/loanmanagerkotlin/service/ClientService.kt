package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.ClientDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.toEntity
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.toDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.ClientRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityNotFoundException

@Service
class ClientService(private val repository: ClientRepository) {
    private val passwordEncoder = BCryptPasswordEncoder()

    @Transactional
    fun register(clientDto: ClientDto): ClientDto {
        clientDto.password = passwordEncoder.encode(clientDto.password)
        return repository.save(clientDto.toEntity()).toDto()
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ClientDto = repository.findByIdOrNull(id)?.toDto()
            ?: throw EntityNotFoundException("Client was not found for the given id: $id")

    @Transactional(readOnly = true)
    fun getAll(): List<ClientDto> = repository.findAll().map { it.toDto() }
}