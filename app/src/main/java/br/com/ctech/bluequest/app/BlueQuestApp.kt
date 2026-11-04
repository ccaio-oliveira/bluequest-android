package br.com.ctech.bluequest.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.ctech.bluequest.data.session.SessionState
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.features.auth.AuthRoute

@Composable
fun BlueQuestApp(viewModel: AppViewModel = hiltViewModel()) {
    val sessionState by viewModel.sessionState.collectAsStateWithLifecycle()

    when (val state = sessionState) {
        SessionState.Loading -> Box(Modifier.fillMaxSize().background(BQColors.bg0))
        is SessionState.SignedOut -> AuthRoute()
        is SessionState.SignedIn -> MainScreen(userName = state.user?.name, onLogout = viewModel::logout)
    }
}