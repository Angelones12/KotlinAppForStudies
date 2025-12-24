package com.example.mjkapp.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PlayersRepository {
    suspend fun getPlayerByEmail(email: String): Player?
    // Dodano nową metodę do pobierania po ID
    suspend fun getPlayerById(id: Long): Player?
    suspend fun insertPlayer(player: Player): Long
    // Zaktualizowano sygnaturę update (dodano description i imageUri)
    suspend fun updatePlayer(email: String, name: String, description: String, imageUri: String?)
}

// Implementacja
class PlayersRepositoryImpl @Inject constructor(private val playerDao: PlayerDao) : PlayersRepository {

    override suspend fun getPlayerByEmail(email: String) = playerDao.getPlayerByEmail(email)

    // Implementacja nowej metody
    override suspend fun getPlayerById(id: Long) = playerDao.getPlayerById(id)

    override suspend fun insertPlayer(player: Player) = playerDao.insert(player)

    // Tutaj parametry muszą się zgadzać z interfejsem powyżej
    override suspend fun updatePlayer(email: String, name: String, description: String, imageUri: String?) {
        playerDao.updatePlayer(email, name, description, imageUri)
    }
}

interface ScoresRepository {
    suspend fun insertScore(score: Score): Long
    fun getAllScoresStream(): Flow<List<PlayerScore>>
}

class ScoresRepositoryImpl @Inject constructor(
    private val scoreDao: ScoreDao,
    private val playerScoreDao: PlayerScoreDao
) : ScoresRepository {
    override suspend fun insertScore(score: Score): Long = scoreDao.insert(score)
    override fun getAllScoresStream(): Flow<List<PlayerScore>> = playerScoreDao.loadPlayersWithScores()
}