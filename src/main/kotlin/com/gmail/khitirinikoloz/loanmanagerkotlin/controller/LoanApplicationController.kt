package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.dto.LoanApplicationDto
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.LoanApplicationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/loans")
class LoanApplicationController(private val loanApplicationService: LoanApplicationService) {

    @PostMapping("/")
    fun create(@Valid @RequestBody loanApplicationDto: LoanApplicationDto) =
            ResponseEntity(loanApplicationService.register(loanApplicationDto), HttpStatus.CREATED)

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long) = ResponseEntity(loanApplicationService.get(id), HttpStatus.OK)

    @GetMapping("/")
    fun getAll() = ResponseEntity(loanApplicationService.getAll(), HttpStatus.OK)

    @GetMapping("/field/{field}/sort/{strategy}")
    fun getAll(@PathVariable("field") field: String, @PathVariable("strategy") strategy: String) =
            ResponseEntity(loanApplicationService.getAllSorted(field, strategy), HttpStatus.OK)


    @GetMapping("/client/{id}")
    fun getAllByClient(@PathVariable("id") id: Long): ResponseEntity<List<LoanApplicationDto>> =
            ResponseEntity(loanApplicationService.getAllByClientId(id), HttpStatus.OK)

    @PutMapping("/{id}")
    fun update(@Valid @RequestBody loanApplicationDto: LoanApplicationDto, @PathVariable("id") id: Long) =
            ResponseEntity(loanApplicationService.update(loanApplicationDto, id), HttpStatus.OK)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long) = ResponseEntity(loanApplicationService.delete(id), HttpStatus.OK)
}