package br.com.ctech.bluequest.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import coil3.compose.AsyncImage

@Composable
fun FeedRow(
    taskName: String,
    points: Int,
    name: String,
    isYou: Boolean,
    timeText: String,
    photoUrl: String?,
    onOpenPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(BQRadius.medium)
    val thumbnailShape = RoundedCornerShape(BQRadius.small)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(BQColors.bg1)
            .border(1.dp, BQColors.stroke1, shape)
            .clickable(enabled = photoUrl != null, role = Role.Button, onClickLabel = "Ver foto", onClick = onOpenPhoto)
            .padding(start = BQSpacing.sp3, end = BQSpacing.cardPadding, top = BQSpacing.sp3, bottom = BQSpacing.sp3),
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Foto de $taskName",
                modifier = Modifier
                    .size(56.dp)
                    .clip(thumbnailShape)
                    .background(BQColors.bg2),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(BQColors.greenDim, thumbnailShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = BQColors.green,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2)) {
                Text(
                    text = taskName,
                    modifier = Modifier.weight(1f),
                    style = BQFont.body(BQTypeScale.body, FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "+${points} pts",
                    style = BQFont.display(12.sp),
                    color = BQColors.amber
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BQAvatar(name = name, size = 22.dp)

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = BQColors.text2)) { append(name)}

                        if (isYou) {
                            withStyle(SpanStyle(color = BQColors.blueBright)) { append(" · você")}
                        }
                    },
                    modifier = Modifier.weight(1f),
                    style = BQFont.body(BQTypeScale.caption),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = timeText,
                    style = BQFont.body(12.sp),
                    color = BQColors.text3,
                )
            }
        }
    }
}