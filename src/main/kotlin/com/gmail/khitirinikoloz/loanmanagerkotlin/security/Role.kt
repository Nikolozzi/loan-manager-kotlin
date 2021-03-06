package com.gmail.khitirinikoloz.loanmanagerkotlin.security

import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role(
        @Id
        @Column(name = "role_id")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int = 0,
        val type: RoleType
)

enum class RoleType { CREATOR, EDITOR }