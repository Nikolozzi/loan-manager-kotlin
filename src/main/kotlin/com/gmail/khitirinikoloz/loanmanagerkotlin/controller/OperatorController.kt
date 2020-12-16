package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.OperatorDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.OperatorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/operator")
class OperatorController(private val operatorService: OperatorService) {

    @PostMapping("/registration")
    fun create(@Valid @RequestBody operatorDto: OperatorDto) =
            ResponseEntity(operatorService.register(operatorDto), HttpStatus.CREATED)

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long) = ResponseEntity(operatorService.getById(id), HttpStatus.OK)

    @GetMapping("/")
    fun getAll() = ResponseEntity(operatorService.getAll(), HttpStatus.OK)
}