package br.com.ctech.bluequest.data.challenges

import br.com.ctech.bluequest.domain.models.ChallengeState
import br.com.ctech.bluequest.domain.models.ChallengeSummary
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import java.time.LocalDate

interface ChallengeApi {
    @GET("challenges")
    suspend fun challenges(): ChallengesResponse
}

@Serializable
data class ChallengesResponse(val challenges: List<ChallengeSummaryDto>)

@Serializable
data class ChallengeSummaryDto(
    val id: Int,
    val name: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    val state: String,
    @SerialName("current_day") val currentDay: Int,
    @SerialName("total_days") val totalDays: Int,
    @SerialName("my_points") val myPoints: Int,
    @SerialName("my_rank") val myRank: Int? = null,
    @SerialName("participants_count") val participantsCount: Int,
    val participants: List<ParticipantSummaryDto>,
) {
    fun toDomain() = ChallengeSummary(
        id = id,
        name = name,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        state = challengeState(state),
        currentDay = currentDay,
        totalDays = totalDays,
        myPoints = myPoints,
        myRank = myRank,
        participantsCount = participantsCount,
        participantNames = participants.map { it.name }
    )
}

@Serializable
data class ParticipantSummaryDto(val name: String)

fun challengeState(apiValue: String): ChallengeState = when (apiValue) {
    "future" -> ChallengeState.Future
    "closed" -> ChallengeState.Closed
    else -> ChallengeState.InProgress
}