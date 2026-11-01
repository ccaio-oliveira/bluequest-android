package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale

@Composable
fun BQErrorToast(message: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(21.dp)

    Row(
        modifier = modifier
            .heightIn(min = 42.dp)
            .background(BQColors.bg2, shape)
            .border(1.dp, BQColors.stroke1, shape)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_warning_fill),
            contentDescription = null,
            tint = BQColors.red,
            modifier = Modifier.size(18.dp)
        )
        Text(text = message, style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold))
    }
}