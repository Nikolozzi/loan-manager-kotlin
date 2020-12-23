package com.gmail.khitirinikoloz.loanmanagerkotlin

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateClientRequest
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
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClientControllerIntegrationTest(@Autowired private val restTemplate: TestRestTemplate) {

    @Autowired
    lateinit var operatorService: OperatorService
    lateinit var operatorRequest: CreateOperatorRequest

    companion object {
        val clientRequest = CreateClientRequest(personalId = "11111111111", firstName = "clientFirstName",
                lastName = "clientLastName", username = "clientUsername", "clientEmail@test.com",
                password = "clientPassword", birthDate = LocalDate.of(1995, 3, 10),
                employer = "clientEmployer", salary = BigDecimal.valueOf(5000), liability = BigDecimal.valueOf(500))
    }

    @BeforeEach
    fun setup() {
        operatorRequest = CreateOperatorRequest(personalId = "22222222222", firstName = "operatorFirstName",
                lastName = "operatorLastName", username = "operatorUsername", email = "operatorEmail",
                password = "operatorPassword", phoneNumber = "operatorNumber")
        operatorService.register(operatorRequest)
    }

    @Test
    fun `Create a new client`() {
        val response = restTemplate.postForEntity("/client/registration", clientRequest, ClientResponse::class.java)

        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val responseBody = response.body
        assertThat(responseBody).isNotNull
        assertAllFields(responseBody)
    }

    @Test
    fun `Fetch an already existing client`() {
        val existingClient = restTemplate.postForEntity("/client/registration",
                clientRequest, ClientResponse::class.java).body

        val response = restTemplate.withBasicAuth(clientRequest.username, clientRequest.password)
                .getForEntity("/client/${existingClient?.id}", ClientResponse::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val responseBody = response.body
        assertThat(responseBody).isNotNull
        assertAllFields(responseBody)
    }

    @Test
    fun `Fetch a non-existing client`() {
        restTemplate.postForEntity("/client/registration", clientRequest, ClientResponse::class.java).body

        val invalidClientId = 100
        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .getForEntity("/client/$invalidClientId", Object::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Fetch an existing client with wrong credentials`() {
        val clientResponse =
                restTemplate.postForEntity("/client/registration", clientRequest, ClientResponse::class.java).body

        val invalidUsername = "invalidUsername"
        val invalidPassword = "invalidPassword"
        val response = restTemplate.withBasicAuth(invalidUsername, invalidPassword)
                .getForEntity("/client/${clientResponse?.id}", Object::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `Fetch all clients`() {
        restTemplate.postForEntity("/client/registration", clientRequest, ClientResponse::class.java)
        val anotherClientRequest = CreateClientRequest(personalId = "22222222222", firstName = "anotherClientFirstName",
                lastName = "anotherClientLastName", username = "anotherClientUsername", "anotherClientEmail@test.com",
                password = "anotherClientPassword", birthDate = LocalDate.of(1995, 3, 10),
                employer = "anotherClientEmployer", salary = BigDecimal.valueOf(5000), liability = BigDecimal.valueOf(500))

        restTemplate.postForEntity("/client/registration", anotherClientRequest, ClientResponse::class.java)

        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .exchange("/client/", HttpMethod.GET, null, object : ParameterizedTypeReference<List<ClientResponse>>() {})

        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.size).isEqualTo(2)
    }

    private fun assertAllFields(clientResponse: ClientResponse?) {
        assertThat(clientResponse?.firstName).isEqualTo(clientRequest.firstName)
        assertThat(clientResponse?.lastName).isEqualTo(clientRequest.lastName)
        assertThat(clientResponse?.firstName).isEqualTo(clientRequest.firstName)
        assertThat(clientResponse?.username).isEqualTo(clientRequest.username)
        assertThat(clientResponse?.email).isEqualTo(clientRequest.email)
        assertThat(clientResponse?.birthDate).isEqualTo(clientRequest.birthDate)
        assertThat(clientResponse?.employer).isEqualTo(clientRequest.employer)
        assertThat(clientResponse?.salary?.compareTo(clientRequest.salary)).isEqualTo(0)
        assertThat(clientResponse?.liability?.compareTo(clientRequest.liability)).isEqualTo(0)
    }
}