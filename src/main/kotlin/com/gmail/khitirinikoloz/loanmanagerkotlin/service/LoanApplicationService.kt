package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.LoanApplicationDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.toEntity
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanApplication
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanStatus
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.toDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.LoanApplicationRepository
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicInteger
import javax.persistence.EntityNotFoundException

@Service
class LoanApplicationService(private val repository: LoanApplicationRepository) {

    @Transactional
    fun register(loanApplicationDto: LoanApplicationDto): LoanApplicationDto {
        val application = loanApplicationDto.toEntity()
        application.generateStatus()
        return repository.save(application).toDto()
    }

    @Transactional(readOnly = true)
    fun get(id: Long) = repository.findByIdOrNull(id)?.toDto()
            ?: throw EntityNotFoundException("Loan application not found for given id: $id")

    @Transactional(readOnly = true)
    fun getAll(): List<LoanApplicationDto> = repository.findAll().map { it.toDto() }

    @Transactional(readOnly = true)
    fun getAllSorted(field: String, direction: String): List<LoanApplicationDto> {
        val sortingStrategy = when (direction.toLowerCase()) {
            "asc" -> Sort.Direction.ASC
            else -> Sort.Direction.DESC
        }

        return repository.findAll(Sort.by(sortingStrategy, field)).map { it.toDto() }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@userSecurity.hasUserId(#id)")
    fun getAllByClientId(id: Long) = repository.findAllByClientId(id).map { it.toDto() }

    @Transactional
    fun update(loanApplicationDto: LoanApplicationDto, id: Long): LoanApplicationDto {
        repository.findByIdOrNull(id) ?: throw EntityNotFoundException("Loan application not found for given id: $id")
        loanApplicationDto.id = id
        return repository.save(loanApplicationDto.toEntity()).toDto()
    }

    @Transactional
    fun delete(id: Long) = repository.delete(
            repository.findByIdOrNull(id)
                    ?: throw EntityNotFoundException("Loan application not found for given id $id")
    )

    private fun LoanApplication.generateStatus() {
        this.score = getScore()
        status = when {
            score!! < 2500 -> LoanStatus.REJECTED
            score!! > 3500 -> LoanStatus.APPROVED
            else -> LoanStatus.MANUAL
        }
    }

    private fun LoanApplication.getScore(): Double {
        val index = AtomicInteger(1)
        val alphabet = ('a'..'z').associateWith { index.getAndIncrement() }
        val nameCharsSum = client.firstName.toLowerCase().sumBy { alphabet[it] ?: 0 }

        return nameCharsSum + client.salary * 1.5 - client.liability * 3 +
                client.birthDate.year - client.birthDate.monthValue - client.birthDate.dayOfYear
    }
}