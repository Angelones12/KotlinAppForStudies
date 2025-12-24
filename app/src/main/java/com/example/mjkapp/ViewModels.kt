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

// 1. PROFILE VIEWMODEL (ZAKTUALIZOWANY O DESCRIPTION)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val playersRepository: PlayersRepository
) : ViewModel() {

    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var description = mutableStateOf("")
    var profileImageUri = mutableStateOf<String?>(null)
    var numColors = mutableStateOf("5")

    // To jest nasze źródło prawdy - kto jest zalogowany
    var currentPlayerId = mutableStateOf<Long?>(null)

    // NOWA FUNKCJA: Odśwież dane TYLKO zalogowanego użytkownika
    fun refreshCurrentUser() {
        val id = currentPlayerId.value ?: return // Jeśli nikt nie jest zalogowany, nic nie rób
        viewModelScope.launch {
            val player = playersRepository.getPlayerById(id)
            player?.let {
                name.value = it.name
                email.value = it.email
                description.value = it.description
                profileImageUri.value = it.profileImageUri
            }
        }
    }

    fun loginOrRegister(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val emailVal = email.value.trim()
            val nameVal = name.value.trim()
            val descVal = description.value.trim()
            val uriVal = profileImageUri.value

            if (emailVal.isNotEmpty() && nameVal.isNotEmpty()) {
                val existingPlayer = playersRepository.getPlayerByEmail(emailVal)
                val playerId = if (existingPlayer != null) {
                    playersRepository.updatePlayer(emailVal, nameVal, descVal, uriVal)
                    existingPlayer.playerId
                } else {
                    playersRepository.insertPlayer(
                        Player(name = nameVal, email = emailVal, description = descVal, profileImageUri = uriVal)
                    )
                }
                currentPlayerId.value = playerId
                onSuccess(playerId)
            }
        }
    }

    fun saveChanges(onSuccess: () -> Unit) {
        loginOrRegister { onSuccess() }
    }

    fun logout() {
        name.value = ""
        email.value = ""
        description.value = ""
        profileImageUri.value = null
        numColors.value = "5"
        currentPlayerId.value = null
    }
}

// 2. GAME VIEWMODEL (BEZ ZMIAN)
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

// 3. RESULTS VIEWMODEL (BEZ ZMIAN - Prawdopodobnie tego brakowało)
@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val scoresRepository: ScoresRepository
) : ViewModel() {
    // Pobieramy strumień wyników z repozytorium
    val uiState: StateFlow<List<PlayerScore>> = scoresRepository.getAllScoresStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}