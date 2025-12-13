package com.calogoal.util

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset

fun LocalDate.toEpochMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()

fun getAge(dob: LocalDate): Int =
    Period.between(dob, LocalDate.now()).years
