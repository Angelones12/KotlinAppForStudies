package com.example.mjkapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // Ważne!
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultsScreen(
    recentScore: Int,
    viewModel: ResultsViewModel, // ZMIANA: Przyjmujemy ViewModel
    onRestartGame: () -> Unit,
    onLogout: () -> Unit
) {
    // ZMIANA: Pobieramy listę wyników z bazy danych (Live updates!) [cite: 202-210]
    val highScores by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Results", style = MaterialTheme.typography.displayLarge, modifier = Modifier.padding(bottom = 16.dp))
        Text("Recent score: $recentScore", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 32.dp))

        // ZMIANA: Tabela wyników
        Text("High Scores", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start))
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 16.dp)
        ) {
            items(highScores) { playerScore ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(playerScore.name, style = MaterialTheme.typography.bodyLarge)
                    Text(playerScore.scoreValue.toString(), style = MaterialTheme.typography.bodyLarge)
                }
                Divider()
            }
        }

        Button(onClick = onRestartGame, modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 8.dp)) {
            Text("Restart game")
        }

        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 8.dp)) {
            Text("Logout")
        }
    }
}