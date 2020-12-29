package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanStatus
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateClientRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateOperatorRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.UpdateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.ClientResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.LoanApplicationResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.ClientService
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.OperatorService
import com.gmail.khitirinikoloz.loanmanagerkotlin.util.LoanApplicationPage
import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper
import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper.assertAllLoanApplicationFields
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoanApplicationControllerIntegrationTest(@Autowired private val restTemplate: TestRestTemplate,
                                               @Autowired private val clientService: ClientService,
                                               @Autowired private val operatorService: OperatorService) {

    private lateinit var loanApplicationRequest: CreateLoanApplicationRequest
    private lateinit var clientRequest: CreateClientRequest
    private lateinit var clientResponse: ClientResponse
    private lateinit var operatorRequest: CreateOperatorRequest

    @BeforeEach
    fun setup() {
        clientRequest = TestHelper.createClientRequests.first
        clientResponse = clientService.register(clientRequest)
        loanApplicationRequest = CreateLoanApplicationRequest(amount = BigDecimal.valueOf(6000),
                termInMonths = 5, clientId = clientResponse.id)

        operatorRequest = TestHelper.createOperatorRequests.first
        operatorService.register(operatorRequest)
    }

    @Test
    fun `Create a new loanApplication with client credentials and assert that response equals request`() {
        val response = restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .postForEntity("/loans/", loanApplicationRequest, LoanApplicationResponse::class.java)

        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val responseBody = response.body
        assertThat(responseBody).isNotNull
        responseBody?.let { assertAllLoanApplicationFields(responseBody, loanApplicationRequest, clientRequest) }
    }

    @Test
    fun `Fetch a loan by id with operator credentials and assert that response equals request`() {
        val existingLoan = restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .postForEntity("/loans/", loanApplicationRequest, LoanApplicationResponse::class.java).body

        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .getForEntity("/loans/${existingLoan?.id}", LoanApplicationResponse::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val responseBody = response.body
        assertThat(responseBody).isNotNull
        responseBody?.let { assertAllLoanApplicationFields(responseBody, loanApplicationRequest, clientRequest) }
    }

    @Test
    fun `Fetch all loans for a client with correct client credentials and assert that response status is 200`() {
        restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .postForEntity("/loans/", loanApplicationRequest, LoanApplicationResponse::class.java)

        val response = restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .exchange("/loans/client/${clientResponse.id}", HttpMethod.GET, null,
                        object : ParameterizedTypeReference<List<LoanApplicationResponse>>() {})
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.size).isEqualTo(1)
    }

    @Test
    fun `Fetch all loans for other client with correct client credentials and assert that response status is 403`() {
        restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .postForEntity("/loans/", loanApplicationRequest, LoanApplicationResponse::class.java)

        val response = restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .exchange("/loans/client/${clientResponse.id + 1}", HttpMethod.GET, null, Object::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `Fetch all loans in descending order sorted by amount and assert that first amount is greater than second`() {
        restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .postForEntity("/loans/", loanApplicationRequest, LoanApplicationResponse::class.java).body
        val anotherLoanApplicationRequest = CreateLoanApplicationRequest(amount = BigDecimal.valueOf(5000),
                termInMonths = 5, clientId = clientResponse.id)
        restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .postForEntity("/loans/", anotherLoanApplicationRequest, LoanApplicationResponse::class.java).body

        val pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "amount"))
        val pagedResponse = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .exchange("/loans/", HttpMethod.GET, HttpEntity(pageRequest),
                        object : ParameterizedTypeReference<LoanApplicationPage<LoanApplicationResponse>>() {})

        assertThat(pagedResponse).isNotNull
        assertThat(pagedResponse.statusCode).isEqualTo(HttpStatus.OK)
        val page = pagedResponse.body
        assertThat(page).isNotNull
        assertThat(page?.totalPages).isEqualTo(1)
        assertThat(page?.sort?.isSorted)
        val loanApplications = page?.content
        assertThat(loanApplications?.size).isEqualTo(2)
        assertThat(loanApplications?.get(0)?.amount?.compareTo(loanApplications[1].amount)).isEqualTo(1)
    }

    @Test
    fun `Update a loanApplication and assert that updated loan matches with the updateLoanRequest`() {
        val existingLoan = restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .postForEntity("/loans/", loanApplicationRequest, LoanApplicationResponse::class.java).body
        val loanUpdateRequest = UpdateLoanApplicationRequest(status = LoanStatus.REJECTED, score = BigDecimal.valueOf(-5))

        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .patchForObject("/loans/${existingLoan?.id}", loanUpdateRequest, Object::class.java)
        assertThat(response).isNotNull

        val updatedLoanApplication = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .getForEntity("/loans/${existingLoan?.id}", LoanApplicationResponse::class.java).body
        assertThat(updatedLoanApplication?.status).isEqualTo(loanUpdateRequest.status)
        assertThat(updatedLoanApplication?.score?.compareTo(loanUpdateRequest.score)).isEqualTo(0)
    }

    @Test
    fun `Delete an existing loan and assert that the total number of loans is decreased by 1`() {
        val existingLoan = restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .postForEntity("/loans/", loanApplicationRequest, LoanApplicationResponse::class.java).body

        val loanApplicationsBeforeDeletion = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .exchange("/loans/", HttpMethod.GET, null,
                        object : ParameterizedTypeReference<LoanApplicationPage<LoanApplicationResponse>>() {}).body?.content

        assertThat(loanApplicationsBeforeDeletion?.size).isEqualTo(1)
        //delete loan
        restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password).delete("/loans/${existingLoan?.id}")
        val loanApplicationsAfterDeletion = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .exchange("/loans/", HttpMethod.GET, null,
                        object : ParameterizedTypeReference<LoanApplicationPage<LoanApplicationResponse>>() {}).body?.content
        assertThat(loanApplicationsAfterDeletion?.size).isEqualTo(0)
    }
}