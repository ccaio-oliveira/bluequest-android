package br.com.ctech.bluequest.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale

@Composable
fun BQEmptyState(
    @DrawableRes icon: Int,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(horizontal = BQSpacing.sp6, vertical = BQSpacing.sp10),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(BQColors.blueDim, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = BQColors.blueBright,
                modifier = Modifier.size(30.dp),
            )
        }

        Spacer(Modifier.height(BQSpacing.sp3))

        Text(
            text = title,
            style = BQFont.display(BQTypeScale.headline, FontWeight.SemiBold),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(BQSpacing.sp2))

        Text(
            text = message,
            modifier = Modifier.widthIn(max = 260.dp),
            style = BQFont.body(14.sp).copy(lineHeight = 20.sp),
            color = BQColors.text3,
            textAlign = TextAlign.Center
        )

        if (action != null) {
            Spacer(Modifier.height(BQSpacing.sp5))
            action()
        }
    }
}