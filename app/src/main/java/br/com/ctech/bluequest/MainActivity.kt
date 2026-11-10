package br.com.ctech.bluequest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import br.com.ctech.bluequest.app.AppViewModel
import br.com.ctech.bluequest.app.BlueQuestApp
import br.com.ctech.bluequest.designsystem.BlueQuestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        if (savedInstanceState == null) {
            handleInviteLink(intent)
        }

        setContent {
            BlueQuestTheme {
                BlueQuestApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleInviteLink(intent)
    }

    private fun handleInviteLink(intent: Intent) {
        val uri = intent.data ?: return

        if (uri.scheme == "bluequest" && uri.host == "invite") {
            uri.lastPathSegment?.let(appViewModel::onInviteLink)
        }
    }
}