package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper
import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper.assertAllClientFields
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateOperatorRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.ClientResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.OperatorService
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClientControllerIntegrationTest(@Autowired private val restTemplate: TestRestTemplate,
                                      @Autowired private val operatorService: OperatorService) {

    lateinit var operatorRequest: CreateOperatorRequest

    companion object {
        val clientRequest = TestHelper.createClientRequests.first
        const val CLIENT_REGISTRATION_URL = "/client/registration"
    }

    @BeforeEach
    fun setup() {
        operatorRequest = TestHelper.createOperatorRequests.first
        operatorService.register(operatorRequest)
    }

    @Test
    fun `Create a new client and assert that response matches to request`() {
        val response = restTemplate.postForEntity(CLIENT_REGISTRATION_URL, clientRequest, ClientResponse::class.java)

        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val responseBody = response.body
        assertThat(responseBody).isNotNull
        responseBody?.let { assertAllClientFields(it, clientRequest) }
    }

    @Test
    fun `Fetch an already existing client and assert that response object matches to the client`() {
        val existingClient = restTemplate.postForEntity(CLIENT_REGISTRATION_URL,
                clientRequest, ClientResponse::class.java).body

        val response = restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .getForEntity("/client/${existingClient?.id}", ClientResponse::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val responseBody = response.body
        assertThat(responseBody).isNotNull
        responseBody?.let { assertAllClientFields(it, clientRequest) }
    }

    @Test
    fun `Fetch a non-existing client and assert that the response status code is equal to 404`() {
        val nonExistingClientId = 100
        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .getForEntity("/client/$nonExistingClientId", Object::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Fetch a client with wrong credentials and assert that the response status code is equal to 401`() {
        val invalidUsername = "invalidUsername"
        val invalidPassword = "invalidPassword"
        val invalidId = 100

        val response = restTemplate.withBasicAuth(invalidUsername, invalidPassword)
                .getForEntity("/client/$invalidId", Object::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `Fetch all clients and assert that response size equals total number of clients`() {
        restTemplate.postForEntity(CLIENT_REGISTRATION_URL, clientRequest, ClientResponse::class.java)
        val anotherClientRequest = TestHelper.createClientRequests.second

        restTemplate.postForEntity(CLIENT_REGISTRATION_URL, anotherClientRequest, ClientResponse::class.java)
        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password).exchange("/client/",
                HttpMethod.GET, null, object : ParameterizedTypeReference<List<ClientResponse>>() {})
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.size).isEqualTo(2)
    }
}