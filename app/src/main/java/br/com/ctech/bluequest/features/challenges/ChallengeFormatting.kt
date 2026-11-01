package br.com.ctech.bluequest.features.challenges

import br.com.ctech.bluequest.domain.models.TaskRecurrence
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dayMonth = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("pt-BR"))
private val hourMinute = DateTimeFormatter.ofPattern("HH:mm")
private val weekdayNames = mapOf(1 to "dom", 2 to "seg", 3 to "ter", 4 to "qua", 5 to "qui", 6 to "sex", 7 to "sáb")

fun periodText(start: LocalDate, end: LocalDate): String {
    val endText = dayMonthText(end)

    if (start.year == end.year && start.month == end.month) {
        return "${start.dayOfMonth}-$endText"
    }

    return "${dayMonthText(start)} - $endText"
}

fun dayMonthText(date: LocalDate): String = date.format(dayMonth).replace(".", "")

fun timeText(time: LocalTime): String = time.format(hourMinute)

fun recurrenceText(recurrence: TaskRecurrence): String = when (recurrence) {
    TaskRecurrence.Daily -> "todos os dias"
    is TaskRecurrence.TimesPerWeek -> "${recurrence.times}x por semana"
    is TaskRecurrence.Dates -> if (recurrence.dates.size > 3) "${recurrence.dates.size} datas"
    else recurrence.dates.joinToString(", ") { dayMonthText(it) }
    is TaskRecurrence.Weekdays -> recurrence.days.sorted().mapNotNull { weekdayNames[it] }.joinToString("/")
}