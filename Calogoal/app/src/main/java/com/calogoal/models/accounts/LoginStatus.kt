package com.calogoal.models.accounts

data class LoginStatus(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)