package br.com.ctech.bluequest

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.BlueQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            BlueQuestTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BQColors.bg0)
                        .safeDrawingPadding()
                        .padding(horizontal = BQSpacing.screenPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Blue")
                            withStyle(SpanStyle(color = BQColors.blueBright)) {
                                append("Quest")
                            }
                        },
                        style = BQFont.display(BQTypeScale.hero),
                    )
                    Spacer(Modifier.height(BQSpacing.sp2))
                    Text("Desafios em grupo, pontos e ranking", color = BQColors.text2)
                }
            }
        }
    }
}