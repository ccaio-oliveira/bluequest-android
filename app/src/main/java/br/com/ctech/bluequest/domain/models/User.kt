package br.com.ctech.bluequest.domain.models

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val hasPassword: Boolean,
)