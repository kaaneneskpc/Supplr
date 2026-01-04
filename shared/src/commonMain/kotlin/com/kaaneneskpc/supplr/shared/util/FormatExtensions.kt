package com.kaaneneskpc.supplr.shared.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatDate(timestamp: Long): String {
    return try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val months = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        "${months[localDateTime.monthNumber - 1]} ${localDateTime.dayOfMonth}, ${localDateTime.year}"
    } catch (_: Exception) {
        "Unknown date"
    }
}

fun formatPrice(price: Double): String {
    val intPart = price.toLong()
    val decPart = ((price - intPart) * 100).toInt()
    return "$intPart.${decPart.toString().padStart(2, '0')}"
}