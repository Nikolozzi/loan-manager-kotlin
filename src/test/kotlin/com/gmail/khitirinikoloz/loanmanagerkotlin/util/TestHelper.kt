package com.gmail.khitirinikoloz.loanmanagerkotlin.util

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.LoanStatus
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateClientRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateOperatorRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.ClientResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.LoanApplicationResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.OperatorResponse
import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal
import java.time.LocalDate

object TestHelper {
    fun assertAllClientFields(clientResponse: ClientResponse, clientRequest: CreateClientRequest) {
        assertThat(clientResponse.id).isNotNull
        assertThat(clientResponse.firstName).isEqualTo(clientRequest.firstName)
        assertThat(clientResponse.lastName).isEqualTo(clientRequest.lastName)
        assertThat(clientResponse.firstName).isEqualTo(clientRequest.firstName)
        assertThat(clientResponse.username).isEqualTo(clientRequest.username)
        assertThat(clientResponse.email).isEqualTo(clientRequest.email)
        assertThat(clientResponse.birthDate).isEqualTo(clientRequest.birthDate)
        assertThat(clientResponse.employer).isEqualTo(clientRequest.employer)
        assertThat(clientResponse.salary.compareTo(clientRequest.salary)).isEqualTo(0)
        assertThat(clientResponse.liability.compareTo(clientRequest.liability)).isEqualTo(0)
    }

    fun assertAllOperatorFields(operatorResponse: OperatorResponse, operatorRequest: CreateOperatorRequest) {
        assertThat(operatorResponse.id).isNotNull
        assertThat(operatorResponse.firstName).isEqualTo(operatorRequest.firstName)
        assertThat(operatorResponse.lastName).isEqualTo(operatorRequest.lastName)
        assertThat(operatorResponse.email).isEqualTo(operatorRequest.email)
        assertThat(operatorResponse.username).isEqualTo(operatorRequest.username)
        assertThat(operatorResponse.phoneNumber).isEqualTo(operatorRequest.phoneNumber)
    }

    fun assertAllLoanApplicationFields(loanApplicationResponse: LoanApplicationResponse,
                                       loanApplicationRequest: CreateLoanApplicationRequest,
                                       clientRequest: CreateClientRequest) {
        assertAllClientFields(loanApplicationResponse.client, clientRequest)
        assertThat(loanApplicationResponse.amount.compareTo(loanApplicationRequest.amount)).isEqualTo(0)
        assertThat(loanApplicationResponse.termInMonths).isEqualTo(loanApplicationRequest.termInMonths)
        assertThat(loanApplicationResponse.id).isNotNull
        assertThat(loanApplicationResponse.status).isInstanceOf(LoanStatus::class.java)
        assertThat(loanApplicationResponse.score).isInstanceOf(BigDecimal::class.java)
    }

    private val firstCreateClientRequest = CreateClientRequest(personalId = "11111111111", firstName = "clientFirstName",
            lastName = "clientLastName", username = "clientUsername", "clientEmail@test.com",
            password = "clientPassword", birthDate = LocalDate.of(1995, 3, 10),
            employer = "clientEmployer", salary = BigDecimal.valueOf(5000), liability = BigDecimal.valueOf(500))

    private val secondCreateClientRequest = CreateClientRequest(personalId = "22222222222", firstName = "anotherClientFirstName",
            lastName = "anotherClientLastName", username = "anotherClientUsername", "anotherClientEmail@test.com",
            password = "anotherClientPassword", birthDate = LocalDate.of(1995, 3, 10),
            employer = "anotherClientEmployer", salary = BigDecimal.valueOf(5000), liability = BigDecimal.valueOf(500))

    private val firstCreateOperatorRequest = CreateOperatorRequest(personalId = "11111222222", firstName = "operatorFirstName",
            lastName = "operatorLastName", email = "operatorEmail", username = "operatorULsername",
            password = "operatorPassword", phoneNumber = "operatorNumber")

    private val secondCreateOperatorRequest = CreateOperatorRequest(personalId = "22222333333", firstName = "anotherOperatorFirstName",
            lastName = "anotherOperatorLastName", email = "anotherOperatorEmail", username = "anotherOperatorUsername",
            password = "anotherOperatorPassword", phoneNumber = "anotherOperatorNumber")

    val createClientRequests = Pair(firstCreateClientRequest, secondCreateClientRequest)
    val createOperatorRequests = Pair(firstCreateOperatorRequest, secondCreateOperatorRequest)
}