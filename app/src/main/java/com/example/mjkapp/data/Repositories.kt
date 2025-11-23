package com.example.mjkapp.data

import kotlinx.coroutines.flow.Flow

interface PlayersRepository {
    suspend fun getPlayerByEmail(email: String): Player?
    suspend fun insertPlayer(player: Player): Long
    suspend fun updatePlayerName(email: String, name: String)
}

class PlayersRepositoryImpl(private val playerDao: PlayerDao) : PlayersRepository {
    override suspend fun getPlayerByEmail(email: String) = playerDao.getPlayerByEmail(email)
    override suspend fun insertPlayer(player: Player) = playerDao.insert(player)
    override suspend fun updatePlayerName(email: String, name: String) = playerDao.updateName(email, name)
}

// --- SCORES REPOSITORY (Poprawione) ---
interface ScoresRepository {
    // ZMIANA: Dodano ": Long" na końcu
    suspend fun insertScore(score: Score): Long
    fun getAllScoresStream(): Flow<List<PlayerScore>>
}

class ScoresRepositoryImpl(
    private val scoreDao: ScoreDao,
    private val playerScoreDao: PlayerScoreDao
) : ScoresRepository {

    // ZMIANA: Dodano ": Long" dla jasności (teraz typy się zgadzają)
    override suspend fun insertScore(score: Score): Long = scoreDao.insert(score)

    override fun getAllScoresStream(): Flow<List<PlayerScore>> = playerScoreDao.loadPlayersWithScores()
}