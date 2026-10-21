package br.com.ctech.bluequest.data.challenges

import br.com.ctech.bluequest.data.network.apiCall
import br.com.ctech.bluequest.domain.models.ChallengeSummary
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepository @Inject constructor(
    private val api: ChallengeApi,
) {
    suspend fun challenges(): List<ChallengeSummary> = apiCall { api.challenges() }.challenges.map { it.toDomain() }
}