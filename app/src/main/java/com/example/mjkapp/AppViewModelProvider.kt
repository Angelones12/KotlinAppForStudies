package com.example.mjkapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mjkapp.GameViewModel
import com.example.mjkapp.MasterAndApplication
import com.example.mjkapp.ProfileViewModel
import com.example.mjkapp.ResultsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Jak stworzyć ProfileViewModel
        initializer {
            ProfileViewModel(masterAndApplication().container.playersRepository)
        }

        // Jak stworzyć GameViewModel
        initializer {
            GameViewModel(masterAndApplication().container.scoresRepository)
        }

        // Jak stworzyć ResultsViewModel
        initializer {
            ResultsViewModel(masterAndApplication().container.scoresRepository)
        }
    }
}

// Funkcja pomocnicza do pobierania obiektu aplikacji [cite: 163-166]
fun CreationExtras.masterAndApplication(): MasterAndApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MasterAndApplication)