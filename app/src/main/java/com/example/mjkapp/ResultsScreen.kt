package com.example.mjkapp

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
// Importy ikon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit

@Composable
fun ResultsScreen(
    recentScore: Int,
    viewModel: ResultsViewModel,
    onRestartGame: () -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit // NOWY PARAMETR
) {
    val highScores by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Results", style = MaterialTheme.typography.displayLarge, modifier = Modifier.padding(bottom = 16.dp))
        Text("Recent score: $recentScore", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 32.dp))

        Text("High Scores", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 16.dp)
        ) {
            items(highScores) { playerScore ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ZDJĘCIE
                    if (playerScore.profileImageUri != null) {
                        AsyncImage(
                            model = Uri.parse(playerScore.profileImageUri),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // DANE (Imię + Opis)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(playerScore.name, style = MaterialTheme.typography.bodyLarge)
                        if (playerScore.description.isNotEmpty()) {
                            Text(playerScore.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                        }
                    }

                    // WYNIK
                    Text(playerScore.scoreValue.toString(), style = MaterialTheme.typography.titleMedium)
                }
                Divider()
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onEditProfile, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit Player")
            }
            Button(onClick = onLogout, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onRestartGame, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Restart game")
        }
    }
}