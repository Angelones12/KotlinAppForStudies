package com.example.mjkapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mjkapp.data.Player
import com.example.mjkapp.data.PlayerScore
import com.example.mjkapp.data.PlayersRepository
import com.example.mjkapp.data.Score
import com.example.mjkapp.data.ScoresRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. PROFILE VIEWMODEL - Obsługuje ekran startowy/profilu
class ProfileViewModel(private val playersRepository: PlayersRepository) : ViewModel() {

    // Pola, które wpisuje użytkownik (stan UI)
    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var numColors = mutableStateOf("5")

    // Przechowujemy ID gracza po zalogowaniu
    var currentPlayerId = mutableStateOf<Long?>(null)

   // Funkcja logowania lub rejestracji [cite: 197-198]
    fun loginOrRegister(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val emailVal = email.value.trim()
            val nameVal = name.value.trim()

            if (emailVal.isNotEmpty() && nameVal.isNotEmpty()) {
                // Sprawdzamy w bazie, czy taki email już istnieje
                val existingPlayer = playersRepository.getPlayerByEmail(emailVal)

                val playerId = if (existingPlayer != null) {
                    // Jeśli gracz istnieje, aktualizujemy imię (jeśli wpisał inne)
                    if (existingPlayer.name != nameVal) {
                        playersRepository.updatePlayerName(emailVal, nameVal)
                    }
                    existingPlayer.playerId
                } else {
                    // Jeśli gracza nie ma, tworzymy nowego
                    playersRepository.insertPlayer(Player(name = nameVal, email = emailVal))
                }

                // Zapisujemy ID i przechodzimy do gry
                currentPlayerId.value = playerId
                onSuccess(playerId)
            }
        }
    }

    // Wyczyszczenie danych formularza przy wylogowaniu
    fun logout() {
        name.value = ""
        email.value = ""
        numColors.value = "5"
        currentPlayerId.value = null
    }
}

// 2. GAME VIEWMODEL - Obsługuje ekran gry
class GameViewModel(private val scoresRepository: ScoresRepository) : ViewModel() {

    // Zapisuje wynik do bazy danych po wygranej [cite: 199]
    fun saveScore(playerId: Long, scoreVal: Int) {
        viewModelScope.launch {
            val score = Score(playerId = playerId, scoreValue = scoreVal)
            scoresRepository.insertScore(score)
        }
    }
}

// 3. RESULTS VIEWMODEL - Obsługuje ekran wyników
class ResultsViewModel(private val scoresRepository: ScoresRepository) : ViewModel() {

    // Pobiera listę wyników jako strumień (Flow)
    // Dzięki stateIn, lista będzie się automatycznie aktualizować na ekranie [cite: 202-210]
    val uiState: StateFlow<List<PlayerScore>> = scoresRepository.getAllScoresStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}