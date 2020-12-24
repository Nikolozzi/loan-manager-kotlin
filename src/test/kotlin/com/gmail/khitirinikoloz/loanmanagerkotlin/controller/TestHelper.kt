package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateClientRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.ClientResponse
import org.assertj.core.api.Assertions.assertThat

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
}