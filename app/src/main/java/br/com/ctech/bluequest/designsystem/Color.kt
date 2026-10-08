package br.com.ctech.bluequest.designsystem

import androidx.compose.ui.graphics.Color

object BQColors {
    // Base - dark-first
    val bg0 = Color(0xFF060B13)          // oklch(0.15 0.02 255)
    val bg1 = Color(0xFF0C141F)          // oklch(0.19 0.025 255)
    val bg2 = Color(0xFF131E2B)          // oklch(0.23 0.03 255)
    val stroke1 = Color(0xFF242F3D)      // oklch(0.30 0.03 255)
    val stroke2 = Color(0xFF364455)      // oklch(0.38 0.035 255)

    val text1 = Color(0xFFF1F4F7)        // oklch(0.965 0.005 250)
    val text2 = Color(0xFF9FA9B4)        // oklch(0.73 0.02 250)
    val text3 = Color(0xFF677380)        // oklch(0.55 0.025 250)

    // Brand
    val blue = Color(0xFF0098FF)         // oklch(0.67 0.19 250)
    val blueBright = Color(0xFF64C1FF)   // oklch(0.78 0.13 242)
    val blueDim = Color(0xFF082F54)      // oklch(0.30 0.08 252)
    val onBlue = Color(0xFF030915)       // oklch(0.14 0.03 255)

    // Gamificação
    val amber = Color(0xFFF0BB3B)        // oklch(0.82 0.15 85)
    val amberDim = Color(0xFF413007)     // oklch(0.32 0.06 85)
    val onAmber = Color(0xFF201300)      // oklch(0.20 0.05 85)

    // Estados
    val green = Color(0xFF4AC680)        // oklch(0.74 0.15 155)
    val greenDim = Color(0xFF09311B)     // oklch(0.28 0.06 155)
    val red = Color(0xFFEC5B57)          // oklch(0.66 0.18 25)
    val redDim = Color(0xFF451816)       // oklch(0.28 0.07 25)

    // Paleta de avatares (Avatar.jsx)
    val avatarPurple = Color(0xFFC77DD8) // oklch(0.7 0.15 320)
    val avatarCyan = Color(0xFF00B9C3)   // oklch(0.7 0.15 200)

    // Medalhas do ranking (RankingRow.jsx)
    val medalSilver = Color(0xFFB9BEC4)  // oklch(0.8 0.01 250)
    val medalBronze = Color(0xFFB27744)  // oklch(0.62 0.1 60)

    // Acentos de módulo (doses mínimas: ícones, linhas de gráfico)
    val teal = Color(0xFF30C6BF)         // oklch(0.75 0.12 190) — nutrição
    val tealDim = Color(0xFF033633)      // oklch(0.30 0.05 190)
    val violet = Color(0xFFB28FEF)       // oklch(0.72 0.14 300) — corpo
    val violetDim = Color(0xFF322647)    // oklch(0.30 0.06 300)

    // Aliases semânticos
    val stateFuture = text3
    val stateAvailable = blueBright
    val stateDone = green
    val stateExpired = red

    fun medal(position: Int): Color = when (position) {
        1 -> amber
        2 -> medalSilver
        3 -> medalBronze
        else -> text3
    }
}