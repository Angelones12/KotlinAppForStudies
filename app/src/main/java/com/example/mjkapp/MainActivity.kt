package com.example.mjkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mjkapp.ui.theme.MjKappTheme
// Dodatkowe importy dla ProfileScreen
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// --- Logika Nawigacji ---
sealed class Screen {
    object Profile : Screen()
    object Game : Screen()
}

// --- Funkcje pomocnicze dla ProfileScreen ---
@Composable
fun OutlinedTextFieldWithError(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    isValid: (String) -> Boolean,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    val isError = !isValid(value) && value.isNotEmpty()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            if (isError) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Błąd",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        supportingText = {
            Text(
                text = if (isError) errorMessage else " ",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.heightIn(min = 16.dp)
            )
        }
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ProfileImageWithPicker(
    profileImageUri: Uri?,
    selectImageOnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(200.dp)) {
        val imageModifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)

        if (profileImageUri != null) {
            AsyncImage(
                model = profileImageUri,
                contentDescription = "Zdjęcie profilowe",
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = "Brak zdjęcia profilowego",
                modifier = imageModifier.padding(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = selectImageOnClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(48.dp)
                .padding(4.dp)
        ) {
            Icon(
                Icons.Filled.Create,
                contentDescription = "Wybierz obraz",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// --- Ekran Profilu ---
@Composable
fun ProfileScreen(
    onNavigateToGame: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var numColors by rememberSaveable { mutableStateOf("5") }
    var profileImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                profileImageUri = uri
            }
        }
    )

    val isValidName: (String) -> Boolean = { it.isNotEmpty() }
    val isValidEmail: (String) -> Boolean = { it.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) }
    val isValidNumColors: (String) -> Boolean = {
        it.toIntOrNull()?.let { num -> num in 5..10 } ?: false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MasterAnd",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        ProfileImageWithPicker(
            profileImageUri = profileImageUri,
            selectImageOnClick = {
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextFieldWithError(
            value = name,
            onValueChange = { name = it },
            label = "Enter name",
            keyboardType = KeyboardType.Text,
            isValid = isValidName,
            errorMessage = "Name can't be empty",
        )

        OutlinedTextFieldWithError(
            value = email,
            onValueChange = { email = it },
            label = "Enter email",
            keyboardType = KeyboardType.Email,
            isValid = isValidEmail,
            errorMessage = "Invalid email format",
        )

        OutlinedTextFieldWithError(
            value = numColors,
            onValueChange = { numColors = it },
            label = "Enter number of colors",
            keyboardType = KeyboardType.Number,
            isValid = isValidNumColors,
            errorMessage = "Must be between 5 and 10",
        )

        Spacer(modifier = Modifier.height(32.dp))

        val isFormValid = isValidName(name) && isValidEmail(email) && isValidNumColors(numColors)

        Button(
            onClick = onNavigateToGame,
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Next")
        }
    }
}


// --- Główna Nawigacja w Activity ---
@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Profile) }

    when (currentScreen) {
        is Screen.Profile -> ProfileScreen(
            onNavigateToGame = {
                currentScreen = Screen.Game
            }
        )
        is Screen.Game -> GameScreen(
            onNavigateBack = {
                currentScreen = Screen.Profile
            }
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MjKappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MjKappTheme {
        ProfileScreen(onNavigateToGame = {})
    }
}