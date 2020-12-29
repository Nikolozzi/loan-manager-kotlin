package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper
import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper.assertAllOperatorFields
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
class OperatorServiceIntegrationTest(@Autowired private val operatorService: OperatorService,
                                     @Autowired private val userRepository: LoanUserRepository) {

    private val operatorRequest = TestHelper.createOperatorRequests.first

    @Test
    fun `Register new operator, assert that loanUser gets created and operatorResponse matches with request`() {
        val savedOperatorResponse = operatorService.register(operatorRequest)
        val savedLoanUser = userRepository.getLoanUSerDetailsByLoanUsername(savedOperatorResponse.username)
        assertThat(savedLoanUser).isNotNull
        assertAllOperatorFields(savedOperatorResponse, operatorRequest)
    }

    @Test
    fun `Register the same operator twice and assert that DataIntegrityViolationException is thrown`() {
        operatorService.register(operatorRequest)
        assertThrows<DataIntegrityViolationException> { operatorService.register(operatorRequest) }
    }

    @Test
    fun `Fetch an existing operator by id and assert that it matches with operatorRequest object`() {
        val existingOperator = operatorService.register(operatorRequest)
        val operatorResponse = operatorService.getById(existingOperator.id)
        assertThat(operatorResponse).isNotNull
        assertAllOperatorFields(operatorResponse, operatorRequest)
    }

    @Test
    fun `Fetch a non-existing operator and assert that EntityNotFoundException is thrown`() {
        val nonExistingOperatorId = 50L
        assertThrows<EntityNotFoundException> { operatorService.getById(nonExistingOperatorId) }
    }

    @Test
    fun `Fetch all operators and assert that response size and content matches with all operators`() {
        operatorService.register(operatorRequest)
        val anotherOperatorRequest = TestHelper.createOperatorRequests.second
        operatorService.register(anotherOperatorRequest)

        val operatorResponses = operatorService.findAll()
        assertThat(operatorResponses.size).isEqualTo(2)
        assertAllOperatorFields(operatorResponses[0], operatorRequest)
        assertAllOperatorFields(operatorResponses[1], anotherOperatorRequest)
    }
}