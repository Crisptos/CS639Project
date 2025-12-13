package com.calogoal.models.dtos

data class ProfileDTO(
    var name: String = "",
    val dateOfBirth: Long = 0L,
    var age: Int = 0,
    var weight: Int = 0,
    var height: Int = 0,
    var sex: Int = 0
)