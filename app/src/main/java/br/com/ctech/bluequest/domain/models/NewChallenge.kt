package br.com.ctech.bluequest.domain.models

import java.time.LocalDate
import java.time.LocalTime

data class NewChallenge(
    val name: String,
    val description: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val timezone: String,
    val tasks: List<NewTask>
)

data class NewTask(
    val name: String,
    val points: Int,
    val recurrence: TaskRecurrence,
    val deadline: LocalTime,
    val requiresPhoto: Boolean,
)