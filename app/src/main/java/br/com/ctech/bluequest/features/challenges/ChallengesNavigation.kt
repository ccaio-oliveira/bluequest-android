package br.com.ctech.bluequest.features.challenges

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.components.BQEmptyState
import br.com.ctech.bluequest.designsystem.components.BQIconButton
import kotlinx.serialization.Serializable

@Serializable
data object ChallengesListKey : NavKey

@Serializable
data class ChallengeDetailKey(val challengeId: Int) : NavKey

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
                ChallengesRoute(onOpenChallenge = { id -> backStack.add(ChallengeDetailKey(id)) })
            }

            entry<ChallengeDetailKey> {
                ChallengeDetailPlaceholder(onBack = { backStack.removeLastOrNull() })
            }
        }
    )

@Composable
private fun ChallengeDetailPlaceholder(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        BQIconButton(
            icon = R.drawable.ic_arrow_back,
            contentDescription = "Voltar",
            onClick = onBack,
            modifier = Modifier.padding(start = BQSpacing.screenPadding, top = BQSpacing.sp2)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            BQEmptyState(
                icon = R.drawable.ic_flag_fill,
                title = "Detalhe em construção",
                message = "O ranking, as tarefas e as pessoas do desafio chegam na próxima etapa."
            )
        }
    }
}