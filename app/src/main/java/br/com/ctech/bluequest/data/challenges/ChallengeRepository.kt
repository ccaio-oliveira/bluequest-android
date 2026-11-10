package br.com.ctech.bluequest.data.challenges

import br.com.ctech.bluequest.data.network.apiCall
import br.com.ctech.bluequest.domain.models.ChallengeDetail
import br.com.ctech.bluequest.domain.models.ChallengeFeedPage
import br.com.ctech.bluequest.domain.models.ChallengeInvite
import br.com.ctech.bluequest.domain.models.ChallengeSummary
import br.com.ctech.bluequest.domain.models.InvitePreview
import br.com.ctech.bluequest.domain.models.NewChallenge
import br.com.ctech.bluequest.domain.models.TodayOccurrence
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepository @Inject constructor(
    private val api: ChallengeApi,
) {
    suspend fun challenges(): List<ChallengeSummary> = apiCall { api.challenges() }.challenges.map { it.toDomain() }

    suspend fun challenge(id: Int): ChallengeDetail = apiCall { api.challenge(id) }.toDomain()

    suspend fun today(): List<TodayOccurrence> = apiCall { api.today() }.occurrences.map { it.toDomain() }

    suspend fun uploadCompletionPhoto(jpeg: ByteArray): String {
        val photo = MultipartBody.Part.createFormData("photo", "foto.jpg", jpeg.toRequestBody("image/jpeg".toMediaType()))
        return apiCall { api.uploadCompletionPhoto(photo) }.path
    }

    suspend fun completeTask(taskId: Int, occurrenceDate: LocalDate, photoPath: String? = null) {
        apiCall { api.complete(CompleteTaskRequest(taskId, occurrenceDate.toString(), photoPath)) }
    }

    suspend fun feed(challengeId: Int, before: Int? = null): ChallengeFeedPage = apiCall { api.feed(challengeId, before) }.toDomain()

    suspend fun createChallenge(challenge: NewChallenge) {
        apiCall { api.createChallenge(challenge.toRequest()) }
    }

    suspend fun invite(challengeId: Int): ChallengeInvite = apiCall { api.invite(challengeId) }.toDomain()

    suspend fun invitePreview(code: String): InvitePreview = apiCall { api.invitePreview(code) }.toDomain()

    suspend fun acceptInvite(code: String): Int = apiCall { api.acceptInvite(code) }.challengeId
}