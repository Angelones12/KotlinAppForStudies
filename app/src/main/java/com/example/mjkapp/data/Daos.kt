package com.example.mjkapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    // Zwraca ID nowego gracza
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(player: Player): Long

    // Aktualizacja imienia gracza, jeśli email już istnieje
    @Query("UPDATE players SET name = :name, description = :description, profileImageUri = :imageUri WHERE email = :email")
    suspend fun updatePlayer(email: String, name: String, description: String, imageUri: String?)

    // Pobranie gracza po emailu (do sprawdzania logowania)
    @Query("SELECT * from players WHERE email = :email")
    suspend fun getPlayerByEmail(email: String): Player?

    // NOWE: Pobieranie gracza po ID (do edycji)
    @Query("SELECT * from players WHERE playerId = :id")
    suspend fun getPlayerById(id: Long): Player?
}

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(score: Score): Long
}

@Dao
interface PlayerScoreDao {
    // Złączenie tabel i pobranie wyników
    // Sortowanie ASC (rosnąco), bo w Mastermind im mniej prób, tym lepiej
    @Query(
        "SELECT players.name, scores.scoreValue, players.description, players.profileImageUri " +
                "FROM players, scores " +
                "WHERE players.playerId = scores.playerId " +
                "ORDER BY scores.scoreValue ASC"
    )
    fun loadPlayersWithScores(): Flow<List<PlayerScore>>
}