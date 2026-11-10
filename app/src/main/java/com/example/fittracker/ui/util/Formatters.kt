package com.example.fittracker.ui.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
private val zone: ZoneId get() = ZoneId.systemDefault()

fun Long.toDateString(): String = Instant.ofEpochMilli(this).atZone(zone).format(dateFormat)

fun Long.toTimeString(): String = Instant.ofEpochMilli(this).atZone(zone).format(timeFormat)

/** Дата и время вида «дд.мм.гггг» + «чч:мм» в миллисекунды или null, если формат неверный. */
fun parseDateTime(date: String, time: String): Long? = try {
    LocalDate.parse(date.trim(), dateFormat)
        .atTime(LocalTime.parse(time.trim(), timeFormat))
        .atZone(zone).toInstant().toEpochMilli()
} catch (e: DateTimeParseException) {
    null
}

/** Начало дня (миллисекунды) для строки «дд.мм.гггг» или null. */
fun parseDay(date: String): Long? = try {
    LocalDate.parse(date.trim(), dateFormat).atStartOfDay(zone).toInstant().toEpochMilli()
} catch (e: DateTimeParseException) {
    null
}

fun formatDuration(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val hours = minutes / 60
    return if (hours > 0) "$hours ч ${minutes % 60} мин" else "$minutes мин"
}

fun formatKg(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
