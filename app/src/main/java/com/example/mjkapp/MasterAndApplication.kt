package com.example.mjkapp

import android.app.Application
import android.content.Context
import com.example.mjkapp.data.*
import dagger.hilt.android.HiltAndroidApp

// ZMIANA: Dodajemy adnotację Hilt, która generuje kod wstrzykiwania zależności [cite: 3-25, 3-28]
@HiltAndroidApp
class MasterAndApplication : Application() {

    // STARY KOD (ZAKOMENTOWANY ZGODNIE Z INSTRUKCJĄ [cite: 3-93]):
    // Hilt automatycznie tworzy kontener aplikacji, więc nie musimy tego robić ręcznie.
    /*
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
    */
}

// STARY KONTENER (ZAKOMENTOWANY):
// Te klasy zostały zastąpione przez moduły Hilt (AppModule.kt), które stworzyliśmy wcześniej.
/*
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
*/