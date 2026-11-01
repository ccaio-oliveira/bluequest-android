package br.com.ctech.bluequest.data.challenges

import br.com.ctech.bluequest.data.network.apiCall
import br.com.ctech.bluequest.domain.models.ChallengeDetail
import br.com.ctech.bluequest.domain.models.ChallengeSummary
import br.com.ctech.bluequest.domain.models.TodayOccurrence
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
    suspend fun completeTask(taskId: Int, occurrenceDate: LocalDate) {
        apiCall { api.complete(CompleteTaskRequest(taskId, occurrenceDate.toString())) }
    }
}