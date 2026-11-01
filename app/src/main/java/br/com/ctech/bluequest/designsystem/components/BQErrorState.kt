package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQSpacing

@Composable
fun BQErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = BQSpacing.sp10),
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp4, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message, color = BQColors.text2, textAlign = TextAlign.Center)
        BQButton(
            title = "Tentar de novo",
            onClick = onRetry,
            icon = R.drawable.ic_refresh,
            variant = BQButtonVariant.Secondary
        )
    }
}