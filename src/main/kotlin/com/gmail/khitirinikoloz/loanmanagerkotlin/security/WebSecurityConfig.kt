package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Bean
    override fun userDetailsService(): UserDetailsService = LoanUserDetailsService()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setPasswordEncoder(passwordEncoder())
        authProvider.setUserDetailsService(userDetailsService())

        return authProvider
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider())
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/client/registration", "/operator/registration")
    }

    override fun configure(http: HttpSecurity) {
        http.httpBasic()
                .and().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/loans/client/{id}", "/client/{id}")
                .access("(@userSecurity.hasUserId(#id) AND hasAuthority('${RoleType.CREATOR}')) OR hasAuthority('${RoleType.EDITOR}')")
                .antMatchers(HttpMethod.POST, "/loans/")
                .hasAnyAuthority(RoleType.CREATOR.toString(), RoleType.EDITOR.toString())
                .antMatchers(HttpMethod.DELETE, "/loans/**").hasAuthority(RoleType.EDITOR.toString())
                .antMatchers(HttpMethod.PATCH, "/loans/**").hasAuthority(RoleType.EDITOR.toString())
                .antMatchers(HttpMethod.GET, "/**").hasAuthority(RoleType.EDITOR.toString())
                .and()
                .csrf().disable()
                .formLogin().disable()
    }
}

@Component("userSecurity")
class UserSecurity {
    fun hasUserId(userId: Long): Boolean =
            (SecurityContextHolder.getContext().authentication.principal as LoanUserDetails).clientId == userId
}