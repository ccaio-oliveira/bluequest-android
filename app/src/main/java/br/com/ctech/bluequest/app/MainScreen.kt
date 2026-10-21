package br.com.ctech.bluequest.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.components.BQTabBarItem
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.components.BQButton
import br.com.ctech.bluequest.designsystem.components.BQButtonVariant
import br.com.ctech.bluequest.designsystem.components.BQEmptyState
import br.com.ctech.bluequest.designsystem.components.BQTabBar
import br.com.ctech.bluequest.features.challenges.ChallengesListKey
import br.com.ctech.bluequest.features.challenges.rememberChallengesEntries

enum class MainTab(val item: BQTabBarItem) {
    Home(BQTabBarItem("Início", R.drawable.ic_home, R.drawable.ic_home_fill)),
    Workout(BQTabBarItem("Treino", R.drawable.ic_fitness_center, R.drawable.ic_fitness_center_fill)),
    Progress(BQTabBarItem("Evolução", R.drawable.ic_monitoring, R.drawable.ic_monitoring_fill)),
    Challenges(BQTabBarItem("Desafios", R.drawable.ic_flag, R.drawable.ic_flag_fill)),
    Profile(BQTabBarItem("Perfil", R.drawable.ic_person, R.drawable.ic_person_fill)),
}

@Composable
fun MainScreen(userName: String?, onLogout: () -> Unit) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Challenges) }
    val challengesBackStack = rememberNavBackStack(ChallengesListKey)
    val challengesEntries = rememberChallengesEntries(challengesBackStack)

    Scaffold(
        containerColor = BQColors.bg0,
        bottomBar = {
            BQTabBar(
                items = MainTab.entries.map { it.item },
                selectedIndex = selectedTab.ordinal,
                onSelect = { selectedTab = MainTab.entries[it] }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            when (selectedTab) {
                MainTab.Home -> BQEmptyState(
                    icon = R.drawable.ic_home_fill,
                    title = "Dashboard em construção",
                    message = "Aqui vão aparecer o treino do dia, seus números da semana e o resumo dos desafios.",
                )

                MainTab.Workout -> BQEmptyState(
                    icon = R.drawable.ic_fitness_center_fill,
                    title = "Treino em construção",
                    message = "Sua ficha, o treino do dia e a execução série a série vão morar aqui.",
                )

                MainTab.Progress -> BQEmptyState(
                    icon = R.drawable.ic_monitoring_fill,
                    title = "Evolução em construção",
                    message = "Gráficos de carga e peso, recordes e avaliações físicas com comparação.",
                )

                MainTab.Challenges -> NavDisplay(
                    entries = challengesEntries,
                    onBack = { challengesBackStack.removeLastOrNull() }
                )

                MainTab.Profile -> BQEmptyState(
                    icon = R.drawable.ic_person_fill,
                    title = "Perfil em construção",
                    message = "Olá, ${userName ?: "..."}! Seus números, a conta e as preferências vão morar aqui.",
                    action = {
                        BQButton(title = "Sair", onClick = onLogout, variant = BQButtonVariant.Danger)
                    }
                )
            }
        }
    }
}