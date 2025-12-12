package com.calogoal.models.accounts

data class User (
    val id: String = "",
    val isAnonymouse: Boolean = true,
    var email: String = "",
    var password: String = "",
)