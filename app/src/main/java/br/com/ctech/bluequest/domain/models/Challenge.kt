package br.com.ctech.bluequest.domain.models

import java.time.LocalDate
import java.time.LocalTime

enum class ChallengeState {
    Future,
    InProgress,
    Closed,
}

data class ChallengeSummary(
    val id: Int,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val state: ChallengeState,
    val currentDay: Int,
    val totalDays: Int,
    val myPoints: Int,
    val myRank: Int?,
    val participantsCount: Int,
    val participantNames: List<String>,
)

data class ChallengeDetail(
    val id: Int,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val state: ChallengeState,
    val currentDay: Int,
    val totalDays: Int,
    val participantsCount: Int,
    val participants: List<ChallengeParticipant>,
    val stats: ChallengeStats,
    val tasks: List<ChallengeTask>,
)

data class ChallengeParticipant(
    val userId: Int,
    val name: String,
    val points: Int,
    val position: Int,
    val joinedAt: LocalDate,
    val isCreator: Boolean,
    val isYou: Boolean,
)

data class ChallengeStats(
    val points: Int,
    val position: Int?,
    val completedCount: Int,
    val expiredCount: Int,
    val streakDays: Int,
)

data class ChallengeTask(
    val id: Int,
    val name: String,
    val points: Int,
    val deadline: LocalTime,
    val hasPhoto: Boolean,
    val recurrence: TaskRecurrence,
)

sealed interface TaskRecurrence {
    data object Daily : TaskRecurrence
    data class Weekdays(val days: List<Int>) : TaskRecurrence
    data class Dates(val dates: List<LocalDate>) : TaskRecurrence
    data class TimesPerWeek(val times: Int) : TaskRecurrence
}