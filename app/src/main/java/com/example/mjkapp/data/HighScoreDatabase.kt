package com.example.mjkapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Definiujemy, jakie encje wchodzą w skład bazy
@Database(entities = [Player::class, Score::class], version = 4, exportSchema = false)
abstract class HighScoreDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao
    abstract fun scoreDao(): ScoreDao
    abstract fun playerScoreDao(): PlayerScoreDao

    companion object {
        @Volatile
        private var Instance: HighScoreDatabase? = null

        fun getDatabase(context: Context): HighScoreDatabase {
            // Jeśli instancja już jest, zwróć ją. Jeśli nie, stwórz nową w bloku synchronized.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    HighScoreDatabase::class.java,
                    "highscore_database"
                )
                    .fallbackToDestructiveMigration() // Opcjonalne: czyści bazę przy zmianie wersji
                    .build()
                    .also { Instance = it }
            }
        }
    }
}