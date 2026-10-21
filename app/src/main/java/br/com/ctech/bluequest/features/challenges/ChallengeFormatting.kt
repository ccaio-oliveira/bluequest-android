package br.com.ctech.bluequest.features.challenges

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dayMonth = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("pt-BR"))

fun periodText(start: LocalDate, end: LocalDate): String {
    val endText = end.format(dayMonth).replace(".", "")

    if (start.year == end.year && start.month == end.month) {
        return "${start.dayOfMonth}-$endText"
    }

    return "${start.format(dayMonth).replace(".", "")} - $endText"
}