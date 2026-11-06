package br.com.ctech.bluequest.features.challenges

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import br.com.ctech.bluequest.features.challenges.create.CreateChallengeRoute
import br.com.ctech.bluequest.features.challenges.detail.ChallengeDetailRoute
import kotlinx.serialization.Serializable

@Serializable
data object ChallengesListKey : NavKey

@Serializable
data class ChallengeDetailKey(val challengeId: Int) : NavKey

@Serializable
data object CreateChallengeKey : NavKey

@Composable
fun rememberChallengesEntries(backStack: NavBackStack<NavKey>): List<NavEntry<NavKey>> =
    rememberDecoratedNavEntries(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<ChallengesListKey> {
                ChallengesRoute(
                    onOpenChallenge = { id -> backStack.add(ChallengeDetailKey(id)) },
                    onCreateChallenge = { backStack.add(CreateChallengeKey) },
                )
            }

            entry<CreateChallengeKey> {
                CreateChallengeRoute(onClose = { backStack.removeLastOrNull()})
            }

            entry<ChallengeDetailKey> { key ->
                ChallengeDetailRoute(
                    challengeId = key.challengeId,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )