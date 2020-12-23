package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name = "users")
data class LoanUserDetails(
        @Id
        @Column(name = "user_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,
        val clientId: Long? = null,
        val loanUsername: String,
        val loanUserPassword: String,
        var enabled: Boolean = true,
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JoinTable(name = "users_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")]
        )
        var roles: MutableSet<Role>
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
            this.roles.map { SimpleGrantedAuthority(it.type.toString()) }.toMutableList()

    override fun getPassword(): String = this.loanUserPassword

    override fun getUsername(): String = this.loanUsername

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = this.enabled
}