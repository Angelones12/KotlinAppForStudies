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
    @Query("UPDATE players SET name = :name WHERE email = :email")
    suspend fun updateName(email: String, name: String)

    // Pobranie gracza po emailu (do sprawdzania logowania)
    @Query("SELECT * from players WHERE email = :email")
    suspend fun getPlayerByEmail(email: String): Player?
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
        "SELECT players.name, scores.scoreValue " +
                "FROM players, scores " +
                "WHERE players.playerId = scores.playerId " +
                "ORDER BY scores.scoreValue ASC"
    )
    fun loadPlayersWithScores(): Flow<List<PlayerScore>>
}