package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper
import com.gmail.khitirinikoloz.loanmanagerkotlin.util.TestHelper.assertAllOperatorFields
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.OperatorResponse
import org.assertj.core.api.Assertions.assertThat
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
class OperatorControllerIntegrationTest(@Autowired private val restTemplate: TestRestTemplate) {

    companion object {
        val operatorRequest = TestHelper.createOperatorRequests.first
        const val OPERATOR_REGISTRATION_URL = "/operator/registration"
    }

    @Test
    fun `Create a new operator and assert that the response matches to request`() {
        val response = restTemplate.postForEntity(OPERATOR_REGISTRATION_URL, operatorRequest, OperatorResponse::class.java)

        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val responseBody = response.body
        assertThat(responseBody).isNotNull
        responseBody?.let { assertAllOperatorFields(responseBody, operatorRequest) }
    }

    @Test
    fun `Fetch an already existing operator and assert that the response matches to an existing operator`() {
        val existingOperator = restTemplate
                .postForEntity(OPERATOR_REGISTRATION_URL, operatorRequest, OperatorResponse::class.java).body

        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .getForEntity("/operator/${existingOperator?.id}", OperatorResponse::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val responseBody = response.body
        assertThat(responseBody).isNotNull
        responseBody?.let { assertAllOperatorFields(responseBody, operatorRequest) }
    }

    @Test
    fun `Fetch a non-existing operator and assert that the status code is equal to 404`() {
        restTemplate.postForEntity(OPERATOR_REGISTRATION_URL, operatorRequest, OperatorResponse::class.java)

        val nonExistingOperatorId = 100
        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password)
                .getForEntity("/operator/$nonExistingOperatorId", Object::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Fetch an operator with wrong credentials and assert that the status code is equal to 401`() {
        val invalidUsername = "invalidOperatorUsername"
        val invalidPassword = "invalidOperatorPassword"
        val operatorId = 1

        val response = restTemplate.withBasicAuth(invalidUsername, invalidPassword)
                .getForEntity("/operator/$operatorId", Object::class.java)
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `Fetch all operators and assert that response size equals total number of operators`() {
        restTemplate.postForEntity(OPERATOR_REGISTRATION_URL, operatorRequest, OperatorResponse::class.java)
        val anotherOperatorRequest = TestHelper.createOperatorRequests.second
        restTemplate.postForEntity(OPERATOR_REGISTRATION_URL, anotherOperatorRequest, OperatorResponse::class.java)

        val response = restTemplate.withBasicAuth(operatorRequest.username, operatorRequest.password).exchange("/operator/",
                HttpMethod.GET, null, object : ParameterizedTypeReference<List<OperatorResponse>>() {})
        assertThat(response).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.size).isEqualTo(2)
    }
}