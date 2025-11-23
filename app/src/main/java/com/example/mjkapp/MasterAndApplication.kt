package com.example.mjkapp

import android.app.Application
import android.content.Context
import com.example.mjkapp.data.*

// Interfejs kontenera
interface AppContainer {
    val playersRepository: PlayersRepository
    val scoresRepository: ScoresRepository
}

// Implementacja kontenera - tworzy repozytoria leniwie (lazy)
class AppDataContainer(private val context: Context) : AppContainer {
    override val playersRepository: PlayersRepository by lazy {
        PlayersRepositoryImpl(HighScoreDatabase.getDatabase(context).playerDao())
    }

    override val scoresRepository: ScoresRepository by lazy {
        ScoresRepositoryImpl(
            HighScoreDatabase.getDatabase(context).scoreDao(),
            HighScoreDatabase.getDatabase(context).playerScoreDao()
        )
    }
}
class MasterAndApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}