package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.TestHelper
import com.gmail.khitirinikoloz.loanmanagerkotlin.TestHelper.assertAllClientFields
import com.gmail.khitirinikoloz.loanmanagerkotlin.security.LoanUserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.annotation.DirtiesContext
import javax.persistence.EntityNotFoundException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClientServiceIntegrationTest(@Autowired private val clientService: ClientService,
                                   @Autowired private val userRepository: LoanUserRepository) {

    private val clientRequest = TestHelper.createClientRequests.first

    @Test
    fun `Register new client, assert that loanUser is created and saved client matches with clientRequest`() {
        val savedClientResponse = clientService.register(clientRequest)
        val savedLoanUser = userRepository.getLoanUSerDetailsByLoanUsername(savedClientResponse.username)
        assertThat(savedLoanUser).isNotNull
        assertAllClientFields(savedClientResponse, clientRequest)
    }

    @Test
    fun `Register the same client twice and assert that DataIntegrityViolationException is thrown`() {
        clientService.register(clientRequest)
        assertThrows<DataIntegrityViolationException> { clientService.register(clientRequest) }
    }

    @Test
    fun `Fetch an existing client by id and assert that it matches with clientRequest object`() {
        val existingClient = clientService.register(clientRequest)
        val clientResponse = clientService.getById(existingClient.id)
        assertThat(clientResponse).isNotNull
        assertAllClientFields(clientResponse, clientRequest)
    }

    @Test
    fun `Fetch a non-existing client and assert that EntityNotFoundException is thrown`() {
        val nonExistingClientId = 100L
        assertThrows<EntityNotFoundException> { clientService.getById(nonExistingClientId) }
    }

    @Test
    fun `Fetch all clients and assert that response size and content matches with all clients`() {
        clientService.register(clientRequest)
        val anotherClientRequest = TestHelper.createClientRequests.second
        clientService.register(anotherClientRequest)

        val clientResponses = clientService.findAll()
        assertThat(clientResponses.size).isEqualTo(2)
        assertAllClientFields(clientResponses[0], clientRequest)
        assertAllClientFields(clientResponses[1], anotherClientRequest)
    }
}