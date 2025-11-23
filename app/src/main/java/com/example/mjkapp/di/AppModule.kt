package com.example.mjkapp.di

import android.content.Context
import com.example.mjkapp.data.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHighScoreDatabase(@ApplicationContext context: Context): HighScoreDatabase {
        return HighScoreDatabase.getDatabase(context)
    }

    @Provides
    fun providePlayerDao(database: HighScoreDatabase): PlayerDao {
        return database.playerDao()
    }

    @Provides
    fun provideScoreDao(database: HighScoreDatabase): ScoreDao {
        return database.scoreDao()
    }

    @Provides
    fun providePlayerScoreDao(database: HighScoreDatabase): PlayerScoreDao {
        return database.playerScoreDao()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPlayersRepository(
        playersRepositoryImpl: PlayersRepositoryImpl
    ): PlayersRepository

    @Binds
    abstract fun bindScoresRepository(
        scoresRepositoryImpl: ScoresRepositoryImpl
    ): ScoresRepository
}