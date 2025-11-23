package com.example.mjkapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mjkapp.data.Player
import com.example.mjkapp.data.PlayerScore
import com.example.mjkapp.data.PlayersRepository
import com.example.mjkapp.data.Score
import com.example.mjkapp.data.ScoresRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// 1. PROFILE VIEWMODEL
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {
    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var numColors = mutableStateOf("5")
    var currentPlayerId = mutableStateOf<Long?>(null)

    fun loginOrRegister(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val emailVal = email.value.trim()
            val nameVal = name.value.trim()

            if (emailVal.isNotEmpty() && nameVal.isNotEmpty()) {
                val existingPlayer = playersRepository.getPlayerByEmail(emailVal)
                val playerId = if (existingPlayer != null) {
                    if (existingPlayer.name != nameVal) {
                        playersRepository.updatePlayerName(emailVal, nameVal)
                    }
                    existingPlayer.playerId
                } else {
                    playersRepository.insertPlayer(Player(name = nameVal, email = emailVal))
                }
                currentPlayerId.value = playerId
                onSuccess(playerId)
            }
        }
    }

    fun logout() {
        name.value = ""
        email.value = ""
        numColors.value = "5"
        currentPlayerId.value = null
    }
}

// 2. GAME VIEWMODEL
@HiltViewModel
class GameViewModel @Inject constructor(
    private val scoresRepository: ScoresRepository
) : ViewModel() {
    fun saveScore(playerId: Long, scoreVal: Int) {
        viewModelScope.launch {
            scoresRepository.insertScore(Score(playerId = playerId, scoreValue = scoreVal))
        }
    }
}

// 3. RESULTS VIEWMODEL
@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val scoresRepository: ScoresRepository
) : ViewModel() {
    val uiState: StateFlow<List<PlayerScore>> = scoresRepository.getAllScoresStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}