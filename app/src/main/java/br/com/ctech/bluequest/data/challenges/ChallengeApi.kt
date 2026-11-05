package br.com.ctech.bluequest.data.challenges

import br.com.ctech.bluequest.domain.models.ChallengeDetail
import br.com.ctech.bluequest.domain.models.ChallengeFeedItem
import br.com.ctech.bluequest.domain.models.ChallengeFeedPage
import br.com.ctech.bluequest.domain.models.ChallengeParticipant
import br.com.ctech.bluequest.domain.models.ChallengeState
import br.com.ctech.bluequest.domain.models.ChallengeStats
import br.com.ctech.bluequest.domain.models.ChallengeSummary
import br.com.ctech.bluequest.domain.models.ChallengeTask
import br.com.ctech.bluequest.domain.models.OccurrenceState
import br.com.ctech.bluequest.domain.models.TaskRecurrence
import br.com.ctech.bluequest.domain.models.TodayOccurrence
import br.com.ctech.bluequest.domain.models.WeeklyProgress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

interface ChallengeApi {
    @GET("challenges")
    suspend fun challenges(): ChallengesResponse

    @GET("challenges/{id}")
    suspend fun challenge(@Path("id") id: Int): ChallengeDetailDto

    @GET("today")
    suspend fun today(): TodayResponse

    @POST("completions")
    suspend fun complete(@Body body: CompleteTaskRequest)

    @Multipart
    @POST("uploads/completion-photo")
    suspend fun uploadCompletionPhoto(@Part photo: MultipartBody.Part): UploadedPhotoResponse

    @GET("challenges/{id}/feed")
    suspend fun feed(@Path("id") id: Int, @Query("before") before: Int?): FeedResponse
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

@Serializable
data class ChallengeDetailDto(
    val id: Int,
    val name: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    val state: String,
    @SerialName("current_day") val currentDay: Int,
    @SerialName("total_days") val totalDays: Int,
    @SerialName("participants_count") val participantsCount: Int,
    val ranking: List<RankedParticipantDto>,
    val me: ChallengeStatsDto,
    val tasks: List<ChallengeTaskDto>,
) {
    fun toDomain() = ChallengeDetail(
        id = id,
        name = name,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        state = challengeState(state),
        currentDay = currentDay,
        totalDays = totalDays,
        participantsCount = participantsCount,
        participants = ranking.map { it.toDomain() },
        stats = me.toDomain(),
        tasks = tasks.map { it.toDomain() },
    )
}

@Serializable
data class RankedParticipantDto(
    @SerialName("user_id") val userId: Int,
    val name: String,
    val points: Int,
    val position: Int,
    @SerialName("joined_at") val joinedAt: String,
    @SerialName("is_creator") val isCreator: Boolean,
    @SerialName("is_you") val isYou: Boolean,
) {
    fun toDomain() = ChallengeParticipant(
        userId = userId,
        name = name,
        points = points,
        position = position,
        joinedAt = LocalDate.parse(joinedAt),
        isCreator = isCreator,
        isYou = isYou,
    )
}

@Serializable
data class ChallengeStatsDto(
    val points: Int,
    val position: Int? = null,
    @SerialName("completed_count") val completedCount: Int,
    @SerialName("expired_count") val expiredCount: Int,
    @SerialName("streak_days") val streakDays: Int,
) {
    fun toDomain() = ChallengeStats(
        points = points,
        position = position,
        completedCount = completedCount,
        expiredCount = expiredCount,
        streakDays = streakDays,
    )
}

@Serializable
data class ChallengeTaskDto(
    val id: Int,
    val name: String,
    val points: Int,
    @SerialName("deadline_time") val deadlineTime: String,
    @SerialName("photo_requirement") val photoRequirement: String,
    @SerialName("recurrence_type") val recurrenceType: String,
    @SerialName("recurrence_weekdays") val recurrenceWeekdays: List<Int>? = null,
    @SerialName("recurrence_dates") val recurrenceDates: List<String>? = null,
    @SerialName("recurrence_times_per_week") val recurrenceTimesPerWeek: Int? = null,
) {
    fun toDomain() = ChallengeTask(
        id = id,
        name = name,
        points = points,
        deadline = LocalTime.parse(deadlineTime),
        hasPhoto = photoRequirement != "none",
        recurrence = when (recurrenceType) {
            "daily" -> TaskRecurrence.Daily
            "weekly" -> TaskRecurrence.TimesPerWeek(recurrenceTimesPerWeek ?: 0)
            "dates" -> TaskRecurrence.Dates(recurrenceDates.orEmpty().map(LocalDate::parse))
            else -> TaskRecurrence.Weekdays(recurrenceWeekdays.orEmpty())
        }
    )
}

@Serializable
data class TodayResponse(val occurrences: List<OccurrenceDto>)

@Serializable
data class OccurrenceDto(
    @SerialName("task_id") val taskId: Int,
    @SerialName("challenge_id") val challengeId: Int,
    @SerialName("challenge_name") val challengeName: String,
    val name: String,
    val points: Int,
    @SerialName("photo_requirement") val photoRequirement: String,
    @SerialName("deadline_at") val deadlineAt: String,
    @SerialName("occurrence_date") val occurrenceDate: String,
    val state: String,
    @SerialName("points_awarded") val pointsAwarded: Int? = null,
    val weekly: WeeklyProgressDto? = null,
) {
    fun toDomain() = TodayOccurrence(
        taskId = taskId,
        challengeId = challengeId,
        challengeName = challengeName,
        name = name,
        points = points,
        pointsAwarded = pointsAwarded,
        state = when (state) {
            "future" -> OccurrenceState.Future
            "completed" -> OccurrenceState.Completed
            "expired" -> OccurrenceState.Expired
            else -> OccurrenceState.Available
        },
        deadline = OffsetDateTime.parse(deadlineAt),
        occurrenceDate = LocalDate.parse(occurrenceDate),
        hasPhoto = photoRequirement != "none",
        weekly = weekly?.toDomain(),
    )
}

@Serializable
data class WeeklyProgressDto(
    val target: Int,
    val done: Int,
    @SerialName("days_left") val daysLeft: Int,
    @SerialName("is_mandatory") val isMandatory: Boolean,
) {
    fun toDomain() = WeeklyProgress(
        target = target,
        done = done,
        daysLeft = daysLeft,
        isMandatory = isMandatory
    )
}

@Serializable
data class CompleteTaskRequest(
    @SerialName("task_id") val taskId: Int,
    @SerialName("occurrence_date") val occurrenceDate: String,
    @SerialName("photo_path") val photoPath: String? = null,
)

@Serializable
data class UploadedPhotoResponse(val path: String)

@Serializable
data class FeedResponse(
    val items: List<FeedItemDto>,
    @SerialName("next_before") val nextBefore: Int? = null,
) {
    fun toDomain() = ChallengeFeedPage(items = items.map { it.toDomain() }, nextBefore = nextBefore)
}

@Serializable
data class FeedItemDto(
    val id: Int,
    val name: String,
    @SerialName("is_you") val isYou: Boolean,
    @SerialName("task_name") val taskName: String,
    val points: Int,
    @SerialName("occurrence_date") val occurrenceDate: String,
    @SerialName("completed_at") val completedAt: String,
    @SerialName("photo_url") val photoUrl: String? = null,
) {
    fun toDomain() = ChallengeFeedItem(
        id = id,
        name = name,
        isYou = isYou,
        taskName = taskName,
        points = points,
        occurrenceDate = LocalDate.parse(occurrenceDate),
        completedAt = OffsetDateTime.parse(completedAt),
        photoUrl = photoUrl,
    )
}