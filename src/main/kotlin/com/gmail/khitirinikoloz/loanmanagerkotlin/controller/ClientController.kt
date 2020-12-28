package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateClientRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/client")
class ClientController(private val clientService: ClientService) {

    @PostMapping("/registration")
    fun create(@RequestBody createClientRequest: CreateClientRequest) =
            ResponseEntity(clientService.register(createClientRequest), HttpStatus.CREATED)

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') OR (hasAuthority('CREATOR') AND @userSecurity.hasUserId(#id))")
    fun get(@PathVariable("id") id: Long) = ResponseEntity(clientService.getById(id), HttpStatus.OK)

    @GetMapping("/")
    fun findAll() = ResponseEntity(clientService.findAll(), HttpStatus.OK)
}