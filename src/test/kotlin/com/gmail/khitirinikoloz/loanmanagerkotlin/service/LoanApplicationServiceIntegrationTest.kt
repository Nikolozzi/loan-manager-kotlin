package com.gmail.khitirinikoloz.loanmanagerkotlin.service

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanStatus
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateClientRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.UpdateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper
import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper.assertAllLoanApplicationFields
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal
import javax.persistence.EntityNotFoundException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoanApplicationServiceIntegrationTest(@Autowired private val loanApplicationService: LoanApplicationService,
                                            @Autowired private val clientService: ClientService) {

    private lateinit var loanApplicationRequest: CreateLoanApplicationRequest
    private val clientRequest: CreateClientRequest = TestHelper.createClientRequests.first

    @BeforeEach
    fun setup() {
        val clientResponse = clientService.register(clientRequest)
        loanApplicationRequest = CreateLoanApplicationRequest(BigDecimal.valueOf(4000), 7, clientResponse.id)
    }

    @Test
    fun `Register a new loanApplication with existing client id and assert that response matches with request`() {
        val loanApplicationResponse = loanApplicationService.register(loanApplicationRequest)
        assertThat(loanApplicationResponse).isNotNull
        assertAllLoanApplicationFields(loanApplicationResponse, loanApplicationRequest, clientRequest)
    }

    @Test
    fun `Register a new loanApplication with a non-existing client and assert that EntityNotFoundException is thrown`() {
        val nonExistingClientId = 50L
        val invalidLoanApplicationRequest = CreateLoanApplicationRequest(BigDecimal.valueOf(1000),
                1, nonExistingClientId)

        assertThrows<EntityNotFoundException> { loanApplicationService.register(invalidLoanApplicationRequest) }
    }

    @Test
    @WithMockUser(authorities = ["EDITOR"])
    fun `Fetch a loanApplication by id and assert that response matches the existing application`() {
        val existingLoanApplication = loanApplicationService.register(loanApplicationRequest)
        val loanApplicationResponse = loanApplicationService.get(existingLoanApplication.id)
        assertThat(loanApplicationResponse).isNotNull
        assertAllLoanApplicationFields(loanApplicationResponse, loanApplicationRequest, clientRequest)
    }

    @Test
    @WithMockUser(authorities = ["EDITOR"])
    fun `Fetch a non-existing loanApplication and assert that EntityNotFoundException is thrown`() {
        val nonExistingLoanApplicationId = 1L
        assertThrows<EntityNotFoundException> { loanApplicationService.get(nonExistingLoanApplicationId) }
    }

    @Test
    @WithMockUser(authorities = ["EDITOR", "CREATOR"])
    fun `Fetch all loanApplications for a client and assert that response size equals total loans`() {
        val loanApplicationResponse = loanApplicationService.register(loanApplicationRequest)
        val loanApplications = loanApplicationService.findAllByClientId(loanApplicationResponse.client.id)
        assertThat(loanApplications.size).isEqualTo(1)
    }

    @Test
    @WithMockUser(authorities = ["EDITOR"])
    fun `Fetch a single item page in descending order by amount and assert that only max loanApplication is returned`() {
        val loanApplicationResponse = loanApplicationService.register(loanApplicationRequest)
        val anotherLoanApplicationRequest = CreateLoanApplicationRequest(BigDecimal.valueOf(3000),
                5, loanApplicationResponse.client.id)
        loanApplicationService.register(anotherLoanApplicationRequest)

        val pageSize = 1
        val pageRequest = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "amount"))
        val pagedLoanApplications = loanApplicationService.findAll(pageRequest)

        assertThat(pagedLoanApplications.size).isEqualTo(pageSize)
        assertThat(pagedLoanApplications.totalElements).isEqualTo(2)
        val loanApplications = pagedLoanApplications.content
        assertThat(loanApplications.size).isEqualTo(1)
        assertThat(loanApplications.first().amount.compareTo(loanApplicationRequest.amount)).isEqualTo(0)
    }

    @Test
    @WithMockUser(authorities = ["EDITOR"])
    fun `Fetch all loanApplications in ascending order by term and assert that loans are sorted correctly`() {
        val loanApplicationResponse = loanApplicationService.register(loanApplicationRequest)
        val anotherLoanApplicationRequest = CreateLoanApplicationRequest(BigDecimal.valueOf(3000),
                5, loanApplicationResponse.client.id)
        loanApplicationService.register(anotherLoanApplicationRequest)

        val pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "termInMonths"))
        val pagedLoanApplications = loanApplicationService.findAll(pageRequest)
        assertThat(pagedLoanApplications.size).isEqualTo(2)
        assertThat(pagedLoanApplications.sort.isSorted)
        val loanApplications = pagedLoanApplications.content
        assertThat(loanApplications[0].termInMonths.compareTo(loanApplications[1].termInMonths)).isEqualTo(-1)
    }

    @Test
    @WithMockUser(authorities = ["EDITOR"])
    fun `Update loanApplication and assert that all fields get updated`() {
        val loanApplicationResponse = loanApplicationService.register(loanApplicationRequest)
        val updateLoanApplicationRequest = UpdateLoanApplicationRequest(LoanStatus.MANUAL, BigDecimal.valueOf(2756.12))
        loanApplicationService.update(loanApplicationResponse.id, updateLoanApplicationRequest)

        val updatedLoanApplication = loanApplicationService.get(loanApplicationResponse.id)
        assertThat(updatedLoanApplication.status).isEqualTo(updateLoanApplicationRequest.status)
        assertThat(updatedLoanApplication.score.compareTo(updateLoanApplicationRequest.score)).isEqualTo(0)
    }

    @Test
    @WithMockUser(authorities = ["EDITOR"])
    fun `Delete a loanApplication and assert that total number of loanApplications is decreased by 1`() {
        val loanApplicationResponse = loanApplicationService.register(loanApplicationRequest)
        val pageRequest = PageRequest.of(0, 1, Sort.by(Sort.DEFAULT_DIRECTION, "amount"))
        val loanApplicationsBeforeDeletion = loanApplicationService.findAll(pageRequest).content
        assertThat(loanApplicationsBeforeDeletion.size).isEqualTo(1)

        loanApplicationService.delete(loanApplicationResponse.id)
        val loanApplicationsAfterDeletion = loanApplicationService.findAll(pageRequest).content
        assertThat(loanApplicationsAfterDeletion.size).isEqualTo(0)
    }
}