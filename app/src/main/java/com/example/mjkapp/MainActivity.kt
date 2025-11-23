package com.example.mjkapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
// ZMIANA: Importujemy hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.mjkapp.ui.theme.MjKappTheme
// ZMIANA: Importujemy AndroidEntryPoint
import dagger.hilt.android.AndroidEntryPoint

// (zakomentowany import starej fabryki)
// import com.example.mjkapp.ui.AppViewModelProvider

object Destinations {
    const val PROFILE_ROUTE = "profile_screen"
    const val GAME_ROUTE = "game_screen/{numColors}/{playerId}"
    const val RESULTS_ROUTE = "results_screen/{score}"
    const val NUM_COLORS_KEY = "numColors"
    const val PLAYER_ID_KEY = "playerId"
    const val SCORE_KEY = "score"
}

// ... (Komponenty OutlinedTextFieldWithError i ProfileImageWithPicker bez zmian) ...
@Composable
fun OutlinedTextFieldWithError(value: String, onValueChange: (String) -> Unit, label: String, keyboardType: KeyboardType, isValid: (String) -> Boolean, errorMessage: String, modifier: Modifier = Modifier) {
    val isError = !isValid(value) && value.isNotEmpty()
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, singleLine = true, isError = isError, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), modifier = modifier.fillMaxWidth(), trailingIcon = { if (isError) Icon(Icons.Filled.Info, contentDescription = "Błąd", tint = MaterialTheme.colorScheme.error) }, supportingText = { Text(if (isError) errorMessage else " ", color = MaterialTheme.colorScheme.error) })
}

@Composable
fun ProfileImageWithPicker(profileImageUri: Uri?, selectImageOnClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(200.dp)) {
        val imageModifier = Modifier.fillMaxSize().clip(CircleShape)
        if (profileImageUri != null) { AsyncImage(model = profileImageUri, contentDescription = null, modifier = imageModifier, contentScale = ContentScale.Crop) } else { Icon(Icons.Default.Help, contentDescription = null, modifier = imageModifier.padding(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        IconButton(onClick = selectImageOnClick, modifier = Modifier.align(Alignment.TopEnd).size(48.dp)) { Icon(Icons.Filled.Create, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
    }
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    profileImageUri: Uri?,
    onImageSelect: (Uri?) -> Unit,
    onNavigateToGame: (Int, Long) -> Unit
) {
    val imagePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(), onResult = onImageSelect)
    val name = viewModel.name.value
    val email = viewModel.email.value
    val numColors = viewModel.numColors.value
    val isValidName: (String) -> Boolean = { it.isNotEmpty() }
    val isValidEmail: (String) -> Boolean = { it.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) }
    val isValidNumColors: (String) -> Boolean = { it.toIntOrNull()?.let { num -> num in 5..10 } ?: false }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("MasterAnd", style = MaterialTheme.typography.displayLarge, modifier = Modifier.padding(bottom = 48.dp))
        ProfileImageWithPicker(profileImageUri = profileImageUri, selectImageOnClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) })
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextFieldWithError(value = name, onValueChange = { viewModel.name.value = it }, label = "Enter name", keyboardType = KeyboardType.Text, isValid = isValidName, errorMessage = "Name can't be empty")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithError(value = email, onValueChange = { viewModel.email.value = it }, label = "Enter email", keyboardType = KeyboardType.Email, isValid = isValidEmail, errorMessage = "Invalid email format")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithError(value = numColors, onValueChange = { viewModel.numColors.value = it }, label = "Enter number of colors", keyboardType = KeyboardType.Number, isValid = isValidNumColors, errorMessage = "Must be between 5 and 10")
        Spacer(modifier = Modifier.height(32.dp))
        val isFormValid = isValidName(name) && isValidEmail(email) && isValidNumColors(numColors)
        Button(onClick = { viewModel.loginOrRegister { playerId -> onNavigateToGame(numColors.toInt(), playerId) } }, enabled = isFormValid, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Start game") }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val profileImageUriState = rememberSaveable { mutableStateOf<Uri?>(null) }

    NavHost(navController = navController, startDestination = Destinations.PROFILE_ROUTE) {
        composable(Destinations.PROFILE_ROUTE) {
            // ZMIANA: Używamy hiltViewModel() zamiast factory
            val profileViewModel = hiltViewModel<ProfileViewModel>()
            // STARY KOD: val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)

            ProfileScreen(
                viewModel = profileViewModel,
                profileImageUri = profileImageUriState.value,
                onImageSelect = { profileImageUriState.value = it },
                onNavigateToGame = { numColors, playerId ->
                    navController.navigate(Destinations.GAME_ROUTE.replace("{${Destinations.NUM_COLORS_KEY}}", numColors.toString()).replace("{${Destinations.PLAYER_ID_KEY}}", playerId.toString()))
                }
            )
        }

        composable(
            route = Destinations.GAME_ROUTE,
            arguments = listOf(navArgument(Destinations.NUM_COLORS_KEY) { type = NavType.IntType }, navArgument(Destinations.PLAYER_ID_KEY) { type = NavType.LongType })
        ) { backStackEntry ->
            val numColors = backStackEntry.arguments?.getInt(Destinations.NUM_COLORS_KEY) ?: 5
            val playerId = backStackEntry.arguments?.getLong(Destinations.PLAYER_ID_KEY) ?: 0L

            // ZMIANA: hiltViewModel()
            val gameViewModel = hiltViewModel<GameViewModel>()
            // STARY KOD: val gameViewModel: GameViewModel = viewModel(factory = AppViewModelProvider.Factory)

            GameScreen(
                numAvailableColors = numColors,
                playerId = playerId,
                viewModel = gameViewModel,
                onNavigateToResults = { score -> navController.navigate(Destinations.RESULTS_ROUTE.replace("{${Destinations.SCORE_KEY}}", score.toString())) },
                onNavigateBackToProfile = { navController.popBackStack(Destinations.PROFILE_ROUTE, inclusive = false) }
            )
        }

        composable(
            route = Destinations.RESULTS_ROUTE,
            arguments = listOf(navArgument(Destinations.SCORE_KEY) { type = NavType.IntType })
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt(Destinations.SCORE_KEY) ?: 0

            // ZMIANA: hiltViewModel()
            val resultsViewModel = hiltViewModel<ResultsViewModel>()
            val profileViewModel = hiltViewModel<ProfileViewModel>() // Hilt zapewni instancję

            // STARY KOD: val resultsViewModel: ResultsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            // STARY KOD: val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)

            ResultsScreen(
                recentScore = score,
                viewModel = resultsViewModel,
                onRestartGame = { navController.popBackStack() },
                onLogout = {
                    profileViewModel.logout()
                    profileImageUriState.value = null
                    navController.popBackStack(Destinations.PROFILE_ROUTE, inclusive = false)
                }
            )
        }
    }
}

// ZMIANA: Dodajemy @AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MjKappTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}