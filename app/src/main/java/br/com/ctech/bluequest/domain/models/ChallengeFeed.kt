package br.com.ctech.bluequest.domain.models

import java.time.LocalDate
import java.time.OffsetDateTime

data class ChallengeFeedItem(
    val id: Int,
    val name: String,
    val isYou: Boolean,
    val taskName: String,
    val points: Int,
    val occurrenceDate: LocalDate,
    val completedAt: OffsetDateTime,
    val photoUrl: String?,
)

data class ChallengeFeedPage(
    val items: List<ChallengeFeedItem>,
    val nextBefore: Int?,
)