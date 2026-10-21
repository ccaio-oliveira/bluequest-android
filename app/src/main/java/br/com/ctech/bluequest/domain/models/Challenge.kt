package br.com.ctech.bluequest.domain.models

import java.time.LocalDate

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