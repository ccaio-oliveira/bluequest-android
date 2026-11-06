package br.com.ctech.bluequest.features.challenges.create

import br.com.ctech.bluequest.domain.models.NewTask
import br.com.ctech.bluequest.domain.models.TaskRecurrence
import java.time.LocalDate
import java.time.LocalTime

enum class TaskRecurrenceMode(val title: String) {
    FixedDays("Dias fixos"),
    TimesPerWeek("Por semana"),
    Dates("Datas")
}

data class TaskDraft(
    val name: String = "",
    val points: String = "",
    val mode: TaskRecurrenceMode = TaskRecurrenceMode.FixedDays,
    val weekdays: Set<Int> = (1..7).toSet(),
    val timesPerWeek: Int = 3,
    val dates: List<LocalDate> = emptyList(),
    val deadline: LocalTime = LocalTime.of(22, 0),
    val requiresPhoto: Boolean = false,
) {
    val pointsValue: Int
        get() = points.toIntOrNull() ?: 0

    val recurrence: TaskRecurrence
        get() = when (mode) {
            TaskRecurrenceMode.FixedDays -> if (weekdays.size == 7) TaskRecurrence.Daily else TaskRecurrence.Weekdays(weekdays.sorted())
            TaskRecurrenceMode.TimesPerWeek -> TaskRecurrence.TimesPerWeek(timesPerWeek)
            TaskRecurrenceMode.Dates -> TaskRecurrence.Dates(dates)
        }

    fun validationError(): String? = when {
        name.isBlank() -> "Dê um nome à tarefa."
        pointsValue < 1 -> "A tarefa precisa valer pelo menos 1 ponto."
        mode == TaskRecurrenceMode.Dates && dates.isEmpty() -> "Adicione pelo menos uma data."
        mode == TaskRecurrenceMode.FixedDays && weekdays.isEmpty() -> "Escolha pelo menos um dia."
        else -> null
    }

    fun toNewTask() = NewTask(
        name = name.trim(),
        points = pointsValue,
        recurrence = recurrence,
        deadline = deadline,
        requiresPhoto = requiresPhoto
    )
}