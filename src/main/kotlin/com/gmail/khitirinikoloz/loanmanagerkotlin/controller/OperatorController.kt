package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateOperatorRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.OperatorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/operator")
class OperatorController(private val operatorService: OperatorService) {

    @PostMapping("/registration")
    fun create(@RequestBody createOperatorRequest: CreateOperatorRequest) =
            ResponseEntity(operatorService.register(createOperatorRequest), HttpStatus.CREATED)

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long) = ResponseEntity(operatorService.getById(id), HttpStatus.OK)

    @GetMapping("/")
    fun findAll() = ResponseEntity(operatorService.findAll(), HttpStatus.OK)
}