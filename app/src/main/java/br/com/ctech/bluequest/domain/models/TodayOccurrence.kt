package br.com.ctech.bluequest.domain.models

import java.time.LocalDate
import java.time.OffsetDateTime

data class TodayOccurrence(
    val taskId: Int,
    val challengeId: Int,
    val challengeName: String,
    val name: String,
    val points: Int,
    val pointsAwarded: Int?,
    val state: OccurrenceState,
    val deadline: OffsetDateTime,
    val occurrenceDate: LocalDate,
    val hasPhoto: Boolean,
    val weekly: WeeklyProgress?,
) {
    val isOptional: Boolean
        get() = state == OccurrenceState.Available && weekly?.isMandatory == false
}

data class WeeklyProgress(
    val target: Int,
    val done: Int,
    val daysLeft: Int,
    val isMandatory: Boolean,
) {
    val remaining: Int
        get() = (target - done).coerceAtLeast(0)
}