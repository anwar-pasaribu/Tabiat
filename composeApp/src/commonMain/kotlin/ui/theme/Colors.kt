/*
 * MIT License
 *
 * Copyright (c) 2024 Anwar Pasaribu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Project Name: Tabiat
 */
package ui.theme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF36693E)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFB7F1BA)
val onPrimaryContainerLight = Color(0xFF002108)
val secondaryLight = Color(0xFF8C4E28)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFFDBC9)
val onSecondaryContainerLight = Color(0xFF331200)
val tertiaryLight = Color(0xFF1C6B50)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFA7F2D1)
val onTertiaryContainerLight = Color(0xFF002116)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFFFFFFF)
val onBackgroundLight = Color(0xFF181D18)
val surfaceLight = Color(0xFFF7FBF2)
val onSurfaceLight = Color(0xFF181D18)
val surfaceVariantLight = Color(0xFFDDE5D9)
val onSurfaceVariantLight = Color(0xFF424940)
val outlineLight = Color(0xFF727970)
val outlineVariantLight = Color(0xFFC1C9BE)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2D322C)
val inverseOnSurfaceLight = Color(0xFFEEF2E9)
val inversePrimaryLight = Color(0xFF9CD49F)
val surfaceDimLight = Color(0xFFD7DBD3)
val surfaceBrightLight = Color(0xFFF7FBF2)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF1F5EC)
val surfaceContainerLight = Color(0xFFEBEFE7)
val surfaceContainerHighLight = Color(0xFFE5E9E1)
val surfaceContainerHighestLight = Color(0xFFE0E4DB)

val primaryDark = Color(0xFF9CD49F)
val onPrimaryDark = Color(0xFF003914)
val primaryContainerDark = Color(0xFF1D5128)
val onPrimaryContainerDark = Color(0xFFB7F1BA)
val secondaryDark = Color(0xFFFFB68D)
val onSecondaryDark = Color(0xFF532200)
val secondaryContainerDark = Color(0xFF6F3813)
val onSecondaryContainerDark = Color(0xFFFFDBC9)
val tertiaryDark = Color(0xFF8BD5B5)
val onTertiaryDark = Color(0xFF003827)
val tertiaryContainerDark = Color(0xFF00513B)
val onTertiaryContainerDark = Color(0xFFA7F2D1)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF101510)
val onBackgroundDark = Color(0xFFE0E4DB)
val surfaceDark = Color(0xFF101510)
val onSurfaceDark = Color(0xFFE0E4DB)
val surfaceVariantDark = Color(0xFF424940)
val onSurfaceVariantDark = Color(0xFFC1C9BE)
val outlineDark = Color(0xFF8B9389)
val outlineVariantDark = Color(0xFF424940)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE0E4DB)
val inverseOnSurfaceDark = Color(0xFF2D322C)
val inversePrimaryDark = Color(0xFF36693E)
val surfaceDimDark = Color(0xFF101510)
val surfaceBrightDark = Color(0xFF363A35)
val surfaceContainerLowestDark = Color(0xFF0B0F0B)
val surfaceContainerLowDark = Color(0xFF181D18)
val surfaceContainerDark = Color(0xFF1C211C)
val surfaceContainerHighDark = Color(0xFF272B26)
val surfaceContainerHighestDark = Color(0xFF313630)

val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)
