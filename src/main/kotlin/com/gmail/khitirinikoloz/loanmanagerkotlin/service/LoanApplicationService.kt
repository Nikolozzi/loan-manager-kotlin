package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.Client
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanApplication
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanStatus
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.UpdateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.toLoanApplicationEntity
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.LoanApplicationResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.toLoanApplicationResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.ClientRepository
import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.LoanApplicationRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import javax.persistence.EntityNotFoundException

@Service
class LoanApplicationService(private val repository: LoanApplicationRepository, private val clientRepository: ClientRepository) {

    @Transactional
    fun register(createLoanApplicationRequest: CreateLoanApplicationRequest): LoanApplicationResponse {
        val client = clientRepository.findByIdOrNull(createLoanApplicationRequest.clientId)
                ?: throw EntityNotFoundException("Could not create loan. Client does not exist")

        val application = createLoanApplicationRequest.toLoanApplicationEntity(client)
        application.score = generateScore(application.client)
        application.status = generateStatus(checkNotNull(application.score))
        return repository.save(application).toLoanApplicationResponse()
    }

    @Transactional(readOnly = true)
    fun get(id: Long) = repository.findByIdOrNull(id)?.toLoanApplicationResponse()
            ?: throw EntityNotFoundException("Loan application not found for given id: $id")

    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<LoanApplicationResponse> =
            repository.findAll(pageable).map(LoanApplication::toLoanApplicationResponse)

    @Transactional(readOnly = true)
    fun findAllByClientId(id: Long) = repository.findAllByClientId(id).map(LoanApplication::toLoanApplicationResponse)

    @Transactional
    fun update(id: Long, updateLoanApplicationRequest: UpdateLoanApplicationRequest) {
        val loanToUpdate = repository.findByIdOrNull(id)
                ?: throw EntityNotFoundException("Could not update. Loan does not exist")

        updateLoanApplicationRequest.score?.let { loanToUpdate.score = it }
        updateLoanApplicationRequest.status?.let { loanToUpdate.status = it }

        repository.save(loanToUpdate)
    }

    @Transactional
    fun delete(id: Long) = repository.delete(
            repository.findByIdOrNull(id)
                    ?: throw EntityNotFoundException("Loan application not found for given id $id")
    )

    private fun generateStatus(score: BigDecimal): LoanStatus {
        return when {
            score < BigDecimal.valueOf(2500) -> LoanStatus.REJECTED
            score > BigDecimal.valueOf(3500) -> LoanStatus.APPROVED
            else -> LoanStatus.MANUAL
        }
    }

    private fun generateScore(client: Client): BigDecimal {
        val index = AtomicInteger(1)
        val alphabet = ('a'..'z').associateWith { index.getAndIncrement() }
        val nameCharsSum = client.firstName.toLowerCase().sumBy { alphabet[it] ?: 0 }

        return BigDecimal.valueOf(nameCharsSum.toLong()) +
                client.salary * BigDecimal.valueOf(1.5) -
                client.liability * BigDecimal.valueOf(3) +
                BigDecimal.valueOf(client.birthDate.year.toLong()) -
                BigDecimal.valueOf(client.birthDate.monthValue.toLong()) -
                BigDecimal.valueOf(client.birthDate.dayOfYear.toLong())
    }
}