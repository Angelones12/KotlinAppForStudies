package com.example.mjkapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true)
    val playerId: Long = 0,
    val name: String,
    val email: String
)

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true)
    val scoreId: Long = 0,
    val playerId: Long,
    val scoreValue: Int
)

// Klasa pośrednicząca do wyświetlania listy wyników (Join)
// Nie ma adnotacji @Entity, bo to tylko wynik zapytania
data class PlayerScore(
    val name: String,
    val scoreValue: Int
)