package br.com.ctech.bluequest.designsystem

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import br.com.ctech.bluequest.R

private val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_semibold, FontWeight.SemiBold),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

object BQFont {
    fun display(size: TextUnit, weight: FontWeight = FontWeight.Bold): TextStyle =
        TextStyle(fontFamily = SpaceGrotesk, fontSize = size, fontWeight = weight)

    fun body(size: TextUnit, weight: FontWeight = FontWeight.Normal): TextStyle =
        TextStyle(fontFamily = FontFamily.Default, fontSize = size, fontWeight = weight)
}

object BQTypeScale {
    val hero = 40.sp
    val title1 = 28.sp
    val title2 = 22.sp
    val headline = 17.sp
    val body = 15.sp
    val caption = 13.sp
    val micro = 11.sp
}