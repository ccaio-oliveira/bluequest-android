package br.com.ctech.bluequest.domain.models

import java.time.LocalDate

data class ChallengeInvite(
    val isEnabled: Boolean,
    val code: String?,
    val link: String?,
)

enum class InviteState { Valid, Invalid, ChallengeClosed, AlreadyParticipant }

data class InvitePreview(
    val state: InviteState,
    val challengeId: Int?,
    val name: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val invitedBy: String?,
    val participantNames: List<String>,
    val participantsCount: Int,
    val totalDays: Int,
    val tasksCount: Int,
    val maxPointsPerDay: Int,
)