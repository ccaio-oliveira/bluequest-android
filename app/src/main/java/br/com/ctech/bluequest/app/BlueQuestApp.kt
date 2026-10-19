package br.com.ctech.bluequest.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.ctech.bluequest.data.session.SessionState
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.components.BQButton
import br.com.ctech.bluequest.designsystem.components.BQButtonVariant
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