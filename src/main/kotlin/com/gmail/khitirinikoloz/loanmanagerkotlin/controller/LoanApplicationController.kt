package com.gmail.khitirinikoloz.loanmanagerkotlin.controller

import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.CreateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.request.UpdateLoanApplicationRequest
import com.gmail.khitirinikoloz.loanmanagerkotlin.model.response.LoanApplicationResponse
import com.gmail.khitirinikoloz.loanmanagerkotlin.service.LoanApplicationService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/loans")
class LoanApplicationController(private val loanApplicationService: LoanApplicationService) {

    @PostMapping("/")
    fun create(@Valid @RequestBody createLoanApplicationRequest: CreateLoanApplicationRequest) =
            ResponseEntity(loanApplicationService.register(createLoanApplicationRequest), HttpStatus.CREATED)

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long) = ResponseEntity(loanApplicationService.get(id), HttpStatus.OK)

    @GetMapping("/")
    fun findAll(@PageableDefault(size = 10) pageable: Pageable) =
            ResponseEntity(loanApplicationService.findAll(pageable), HttpStatus.OK)

    @GetMapping("/client/{id}")
    fun findAllByClient(@PathVariable("id") id: Long): ResponseEntity<List<LoanApplicationResponse>> =
            ResponseEntity(loanApplicationService.findAllByClientId(id), HttpStatus.OK)

    @PatchMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody updateLoanApplicationRequest: UpdateLoanApplicationRequest) =
            ResponseEntity(loanApplicationService.update(id, updateLoanApplicationRequest), HttpStatus.OK)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long) = ResponseEntity(loanApplicationService.delete(id), HttpStatus.OK)
}