package com.example.mjkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val playerId: Long = 0,
    val name: String,
    val email: String,
    val description: String = "",
    val profileImageUri: String? = null // NOWE POLE
)

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true)
    val scoreId: Long = 0,
    val playerId: Long,
    val scoreValue: Int
)

// Zaktualizowana klasa do listy wyników
data class PlayerScore(
    val name: String,
    val scoreValue: Int,
    val description: String = "",      // NOWE
    val profileImageUri: String? = null // NOWE
)