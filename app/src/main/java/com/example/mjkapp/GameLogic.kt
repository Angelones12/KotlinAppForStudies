package com.example.mjkapp

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

// Klasa danych przechowująca pojedynczą próbę gracza
data class GameAttempt(
    val guess: List<Color>,
    val feedback: List<Color>
)

// Zestaw dostępnych kolorów dla MasterAnd
val AvailableColors = listOf(
    Color.Red,
    Color.Green,
    Color.Blue,
    Color.Yellow,
    Color.Cyan,
    Color.Magenta,
    Color.LightGray,
    Color.DarkGray
)

// Funkcja selectNextAvailableColor (logika)
fun selectNextAvailableColor(
    availableColors: List<Color>,
    selectedColors: List<Color>,
    buttonIndex: Int
): Color {
    val currentColor = selectedColors.getOrElse(buttonIndex) { Color.Transparent }
    val currentSelection = selectedColors.toMutableList()
    currentSelection[buttonIndex] = Color.Transparent

    val forbiddenColors = currentSelection.filter { it != Color.Transparent }
    val available = availableColors.filter { it !in forbiddenColors }

    if (available.isEmpty()) return Color.Transparent

    val currentIndex = available.indexOf(currentColor)
    val nextIndex = (currentIndex + 1) % available.size

    return available[nextIndex]
}

// Funkcja selectRandomColors (logika)
fun selectRandomColors(availableColors: List<Color>): List<Color> {
    require(availableColors.size >= 4) { "Musi być co najmniej 4 unikalne kolory" }
    return availableColors.shuffled(Random.Default).take(4)
}

// Funkcja checkColors (logika)
fun checkColors(
    selectedColors: List<Color>,
    correctColors: List<Color>,
    unfoundColor: Color
): List<Color> {
    val feedback = mutableListOf<Color>()
    val correctColorsUsed = BooleanArray(4) { false }
    val selectedColorsUsed = BooleanArray(4) { false }

    // 1. Sprawdzanie Czerwonych (Właściwy kolor na właściwym miejscu)
    for (i in 0..3) {
        if (selectedColors[i] == correctColors[i]) {
            feedback.add(Color.Red)
            correctColorsUsed[i] = true
            selectedColorsUsed[i] = true
        }
    }

    // 2. Sprawdzanie Żółtych (Właściwy kolor, niewłaściwe miejsce)
    for (i in 0..3) {
        if (!selectedColorsUsed[i]) {
            for (j in 0..3) {
                if (!correctColorsUsed[j] && selectedColors[i] == correctColors[j]) {
                    feedback.add(Color.Yellow)
                    correctColorsUsed[j] = true
                    selectedColorsUsed[i] = true
                    break
                }
            }
        }
    }

    // 3. Wypełnianie kolorem tła
    while (feedback.size < 4) {
        feedback.add(unfoundColor)
    }

    return feedback.shuffled()
}