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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.mjkapp.ui.theme.MjKappTheme
import dagger.hilt.android.AndroidEntryPoint

object Destinations {
    const val ROOT_ROUTE = "root_graph"
    const val PROFILE_ROUTE = "profile_screen"
    const val GAME_ROUTE = "game_screen/{numColors}/{playerId}"
    const val RESULTS_ROUTE = "results_screen/{score}"

    // ZMIANA: Usunęliśmy parametr ID. Teraz to po prostu "edit_profile"
    const val EDIT_PROFILE_ROUTE = "edit_profile"

    const val NUM_COLORS_KEY = "numColors"
    const val PLAYER_ID_KEY = "playerId"
    const val SCORE_KEY = "score"
}

// --- KOMPONENTY POMOCNICZE (Bez zmian) ---

@Composable
fun OutlinedTextFieldWithError(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    isValid: (String) -> Boolean,
    errorMessage: String,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isError = !isValid(value) && value.isNotEmpty()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            if (isError) Icon(Icons.Filled.Info, contentDescription = "Błąd", tint = MaterialTheme.colorScheme.error)
        },
        supportingText = {
            Text(if (isError) errorMessage else " ", color = MaterialTheme.colorScheme.error)
        }
    )
}

@Composable
fun ProfileImageWithPicker(
    profileImageUri: Uri?,
    selectImageOnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(200.dp)) {
        val imageModifier = Modifier.fillMaxSize().clip(CircleShape)
        if (profileImageUri != null) {
            AsyncImage(model = profileImageUri, contentDescription = null, modifier = imageModifier, contentScale = ContentScale.Crop)
        } else {
            Icon(Icons.Default.Help, contentDescription = null, modifier = imageModifier.padding(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = selectImageOnClick, modifier = Modifier.align(Alignment.TopEnd).size(48.dp)) {
            Icon(Icons.Filled.Create, contentDescription = "Zmień zdjęcie", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

// --- EKRAN STARTOWY (LOGOWANIE) ---

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    profileImageUri: Uri?,
    onImageSelect: (Uri?) -> Unit,
    onNavigateToGame: (Int, Long) -> Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onImageSelect
    )

    val name = viewModel.name.value
    val description = viewModel.description.value
    val email = viewModel.email.value
    val numColors = viewModel.numColors.value

    val isValidName: (String) -> Boolean = { it.isNotEmpty() }
    val isValidEmail: (String) -> Boolean = { it.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) }
    val isValidNumColors: (String) -> Boolean = { it.toIntOrNull()?.let { num -> num in 5..10 } ?: false }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("MasterAnd", style = MaterialTheme.typography.displayLarge, modifier = Modifier.padding(bottom = 48.dp))

        ProfileImageWithPicker(
            profileImageUri = profileImageUri,
            selectImageOnClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextFieldWithError(value = name, onValueChange = { viewModel.name.value = it }, label = "Imię", keyboardType = KeyboardType.Text, isValid = isValidName, errorMessage = "Imię nie może być puste")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = description, onValueChange = { viewModel.description.value = it }, label = { Text("Motto (Opcjonalne)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithError(value = email, onValueChange = { viewModel.email.value = it }, label = "Email", keyboardType = KeyboardType.Email, isValid = isValidEmail, errorMessage = "Błędny format email")
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextFieldWithError(value = numColors, onValueChange = { viewModel.numColors.value = it }, label = "Liczba kolorów (5-10)", keyboardType = KeyboardType.Number, isValid = isValidNumColors, errorMessage = "Musi być między 5 a 10")
        Spacer(modifier = Modifier.height(32.dp))

        val isFormValid = isValidName(name) && isValidEmail(email) && isValidNumColors(numColors)
        Button(
            onClick = { viewModel.loginOrRegister { playerId -> onNavigateToGame(numColors.toInt(), playerId) } },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Start Game")
        }
    }
}

// --- EKRAN EDYCJI (BEZ ARGUMENTU ID) ---

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onSaveChanges: () -> Unit,
    onCancel: () -> Unit
) {
    // ZMIANA: Nie bierzemy ID z nawigacji.
    // Prosimy ViewModel, aby odświeżył dane AKTUALNIE ZALOGOWANEGO gracza.
    LaunchedEffect(Unit) {
        viewModel.refreshCurrentUser()
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) viewModel.profileImageUri.value = uri.toString() }
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Edytuj Profil", style = MaterialTheme.typography.displayMedium, modifier = Modifier.padding(bottom = 24.dp))

        val currentUri = viewModel.profileImageUri.value?.let { Uri.parse(it) }
        ProfileImageWithPicker(
            profileImageUri = currentUri,
            selectImageOnClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(value = viewModel.name.value, onValueChange = { viewModel.name.value = it }, label = { Text("Imię") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = viewModel.description.value, onValueChange = { viewModel.description.value = it }, label = { Text("Twoje Motto") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = viewModel.email.value, onValueChange = {}, label = { Text("Email (nieedytowalny)") }, readOnly = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onCancel, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors()) { Text("Anuluj") }
            Button(onClick = { viewModel.saveChanges(onSuccess = onSaveChanges) }, modifier = Modifier.weight(1f)) { Text("Zapisz") }
        }
    }
}

// --- NAWIGACJA ---

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginProfileImageUri = rememberSaveable { mutableStateOf<Uri?>(null) }

    NavHost(navController = navController, startDestination = Destinations.ROOT_ROUTE) {

        navigation(startDestination = Destinations.PROFILE_ROUTE, route = Destinations.ROOT_ROUTE) {

            // 1. PROFILE
            composable(Destinations.PROFILE_ROUTE) { backStackEntry ->
                val sharedViewModel = getSharedProfileViewModel(navController, backStackEntry)
                ProfileScreen(
                    viewModel = sharedViewModel,
                    profileImageUri = loginProfileImageUri.value,
                    onImageSelect = { uri ->
                        loginProfileImageUri.value = uri
                        if (uri != null) sharedViewModel.profileImageUri.value = uri.toString()
                    },
                    onNavigateToGame = { numColors, playerId ->
                        navController.navigate(Destinations.GAME_ROUTE.replace("{${Destinations.NUM_COLORS_KEY}}", numColors.toString()).replace("{${Destinations.PLAYER_ID_KEY}}", playerId.toString()))
                    }
                )
            }

            // 2. GAME
            composable(
                route = Destinations.GAME_ROUTE,
                arguments = listOf(navArgument(Destinations.NUM_COLORS_KEY) { type = NavType.IntType }, navArgument(Destinations.PLAYER_ID_KEY) { type = NavType.LongType })
            ) { backStackEntry ->
                val numColors = backStackEntry.arguments?.getInt(Destinations.NUM_COLORS_KEY) ?: 5
                val playerId = backStackEntry.arguments?.getLong(Destinations.PLAYER_ID_KEY) ?: 0L
                val gameViewModel = hiltViewModel<GameViewModel>()
                val sharedProfileViewModel = getSharedProfileViewModel(navController, backStackEntry)

                GameScreen(
                    numAvailableColors = numColors,
                    playerId = playerId,
                    viewModel = gameViewModel,
                    onNavigateToResults = { score -> navController.navigate(Destinations.RESULTS_ROUTE.replace("{${Destinations.SCORE_KEY}}", score.toString())) },
                    onNavigateBackToProfile = {
                        sharedProfileViewModel.logout()
                        loginProfileImageUri.value = null
                        navController.popBackStack(Destinations.PROFILE_ROUTE, inclusive = false)
                    }
                )
            }

            // 3. RESULTS
            composable(
                route = Destinations.RESULTS_ROUTE,
                arguments = listOf(navArgument(Destinations.SCORE_KEY) { type = NavType.IntType })
            ) { backStackEntry ->
                val score = backStackEntry.arguments?.getInt(Destinations.SCORE_KEY) ?: 0
                val resultsViewModel = hiltViewModel<ResultsViewModel>()
                val sharedProfileViewModel = getSharedProfileViewModel(navController, backStackEntry)

                ResultsScreen(
                    recentScore = score,
                    viewModel = resultsViewModel,
                    onRestartGame = { navController.popBackStack() },
                    onLogout = {
                        sharedProfileViewModel.logout()
                        loginProfileImageUri.value = null
                        navController.popBackStack(Destinations.PROFILE_ROUTE, inclusive = false)
                    },
                    onEditProfile = {
                        // ZMIANA: Nawigujemy bez parametrów.
                        // ViewModel wie, kto jest zalogowany.
                        navController.navigate(Destinations.EDIT_PROFILE_ROUTE)
                    }
                )
            }

            // 4. EDIT PROFILE (ZMIANA: Bez argumentów ID)
            composable(route = Destinations.EDIT_PROFILE_ROUTE) { backStackEntry ->
                val sharedViewModel = getSharedProfileViewModel(navController, backStackEntry)

                EditProfileScreen(
                    viewModel = sharedViewModel,
                    onSaveChanges = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun getSharedProfileViewModel(navController: NavController, backStackEntry: NavBackStackEntry): ProfileViewModel {
    val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Destinations.ROOT_ROUTE) }
    return hiltViewModel(parentEntry)
}

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