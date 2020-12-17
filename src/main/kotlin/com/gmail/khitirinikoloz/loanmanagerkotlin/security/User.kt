package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import javax.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id
        @Column(name = "user_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int = 0,
        var username: String,
        var password: String,
        var enabled: Boolean = true,
        @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @JoinTable(name = "users_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")]
        )
        var roles: MutableSet<Role>
)