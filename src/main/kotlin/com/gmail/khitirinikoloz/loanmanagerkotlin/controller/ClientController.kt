package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.ClientDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/client")
class ClientController(private val clientService: ClientService) {

    @PostMapping("/registration")
    fun create(@Valid @RequestBody clientDto: ClientDto) =
            ResponseEntity(clientService.register(clientDto), HttpStatus.CREATED)

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long) = ResponseEntity(clientService.getById(id), HttpStatus.OK)

    @GetMapping("/")
    fun getAll() = ResponseEntity(clientService.getAll(), HttpStatus.OK)
}