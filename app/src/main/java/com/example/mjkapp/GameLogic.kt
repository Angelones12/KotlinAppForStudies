// angelones12/kotlinappforstudies/KotlinAppForStudies-b96ec3468917a2671c5912202db414b554475358/app/src/main/java/com/example/mjkapp/GameLogic.kt
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

// Funkcja selectNextAvailableColor (logika) - bez zmian
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

// Funkcja selectRandomColors (logika) - bez zmian
fun selectRandomColors(availableColors: List<Color>, numAvailableColors: Int): List<Color> {
    val pool = availableColors.take(numAvailableColors)
    require(pool.size >= 4) { "Musi być co najmniej 4 unikalne kolory do wylosowania kodu" }
    return pool.shuffled(Random.Default).take(4)
}

// --- POCZĄTEK ZMIAN W "checkColors" ---

/**
 * Sprawdza zgadywane kolory i zwraca listę podpowiedzi.
 *
 * @param selectedColors Lista kolorów wybrana przez gracza.
 * @param correctColors Sekretny kod (poprawna lista kolorów).
 * @return Lista 4 kolorów podpowiedzi (Zielony, Żółty lub Czerwony).
 */
fun checkColors(
    selectedColors: List<Color>,
    correctColors: List<Color>
): List<Color> {

    // --- POPRAWKA: Nowe definicje kolorów ---
    val PERFECT_MATCH = Color.Green  // Trafione (dobry kolor, dobre miejsce)
    val PARTIAL_MATCH = Color.Yellow // Dobry kolor na złym miejscu
    val NO_MATCH = Color.Red       // Missmatch (pudło)
    // --- KONIEC POPRAWKI ---

    // Tablice śledzące, które piny zostały już użyte do przyznania punktu
    val codeUsed = BooleanArray(4) { false }
    val guessUsed = BooleanArray(4) { false }

    // --- POPRAWKA: Wynikowa lista domyślnie wypełniona kolorem "pudła" ---
    val feedback = MutableList(4) { NO_MATCH }

    // 1. Pętla sprawdzająca IDEALNE TRAFIENIA (Zielone kółka)
    for (i in 0..3) {
        if (selectedColors[i] == correctColors[i]) {
            feedback[i] = PERFECT_MATCH
            codeUsed[i] = true
            guessUsed[i] = true
        }
    }

    // 2. Pętla sprawdzająca CZĘŚCIOWE TRAFIENIA (Żółte kółka)
    for (i in 0..3) {
        if (!guessUsed[i]) {
            for (j in 0..3) {
                if (!codeUsed[j] && selectedColors[i] == correctColors[j]) {
                    feedback[i] = PARTIAL_MATCH
                    codeUsed[j] = true
                    break
                }
            }
        }
    }

    // 3. Zwracamy listę podpowiedzi (bez mieszania)
    return feedback
}