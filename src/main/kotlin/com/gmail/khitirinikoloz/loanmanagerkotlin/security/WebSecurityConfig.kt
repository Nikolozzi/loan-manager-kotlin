package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import com.gmail.khitirinikoloz.loanmanagerkotlin.repository.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.authenticationProvider(authenticationProvider())
    }

    override fun configure(web: WebSecurity?) {
        web?.ignoring()?.antMatchers("/client/registration", "/operator/registration")
    }

    override fun configure(http: HttpSecurity) {
        http.httpBasic()
                .and().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/loans/client/**", "/client/{id}")
                .hasAnyAuthority(RoleType.CREATOR.toString(), RoleType.EDITOR.toString())
                .antMatchers(HttpMethod.POST, "/loans/")
                .hasAnyAuthority(RoleType.CREATOR.toString(), RoleType.EDITOR.toString())
                .antMatchers(HttpMethod.GET, "/**").hasAuthority(RoleType.EDITOR.toString())
                .antMatchers(HttpMethod.DELETE, "/loans/**").hasAuthority(RoleType.EDITOR.toString())
                .antMatchers(HttpMethod.PATCH, "/loans/**").hasAuthority(RoleType.EDITOR.toString())
                .and()
                .csrf().disable()
                .formLogin().disable()
    }
}

@Component("userSecurity")
class UserSecurity {
    @Autowired
    private lateinit var clientRepository: ClientRepository

    fun hasUserId(userId: Long): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication

        return SimpleGrantedAuthority(RoleType.EDITOR.toString()) in authentication.authorities ||
                clientRepository.findByIdOrNull(userId)?.username == authentication.name
    }
}