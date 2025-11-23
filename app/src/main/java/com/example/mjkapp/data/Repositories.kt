package com.example.mjkapp.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface PlayersRepository {
    suspend fun getPlayerByEmail(email: String): Player?
    suspend fun insertPlayer(player: Player): Long
    suspend fun updatePlayerName(email: String, name: String)
}

// ZMIANA: Dodano @Inject constructor
class PlayersRepositoryImpl @Inject constructor(private val playerDao: PlayerDao) : PlayersRepository {
    override suspend fun getPlayerByEmail(email: String) = playerDao.getPlayerByEmail(email)
    override suspend fun insertPlayer(player: Player) = playerDao.insert(player)
    override suspend fun updatePlayerName(email: String, name: String) = playerDao.updateName(email, name)
}

interface ScoresRepository {
    suspend fun insertScore(score: Score): Long
    fun getAllScoresStream(): Flow<List<PlayerScore>>
}

// ZMIANA: Dodano @Inject constructor
class ScoresRepositoryImpl @Inject constructor(
    private val scoreDao: ScoreDao,
    private val playerScoreDao: PlayerScoreDao
) : ScoresRepository {
    override suspend fun insertScore(score: Score): Long = scoreDao.insert(score)
    override fun getAllScoresStream(): Flow<List<PlayerScore>> = playerScoreDao.loadPlayersWithScores()
}