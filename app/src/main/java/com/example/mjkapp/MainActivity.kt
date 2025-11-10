// angelones12/kotlinappforstudies/KotlinAppForStudies-b96ec3468917a2671c5912202db414b554475358/app/src/main/java/com/example/mjkapp/MainActivity.kt
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

// Dodatkowe importy dla animacji
import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

// Dodatkowe importy dla nawigacji
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument

object Destinations {
    const val PROFILE_ROUTE = "profile_screen"
    const val GAME_ROUTE = "game_screen/{numColors}"
    const val RESULTS_ROUTE = "results_screen/{score}"
    const val NUM_COLORS_KEY = "numColors"
    const val SCORE_KEY = "score"
}

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

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    numColors: String,
    profileImageUri: Uri?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNumColorsChange: (String) -> Unit,
    onImageSelect: (Uri?) -> Unit,
    onNavigateToGame: (Int) -> Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = onImageSelect
    )
    val isValidName: (String) -> Boolean = { it.isNotEmpty() }
    val isValidEmail: (String) -> Boolean = { it.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) }
    val isValidNumColors: (String) -> Boolean = {
        it.toIntOrNull()?.let { num -> num in 5..10 } ?: false
    }
    val infiniteTransition = rememberInfiniteTransition(label = "titleInfiniteTransition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "titleScale"
    )
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
            modifier = Modifier
                .padding(bottom = 48.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                }
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
            onValueChange = onNameChange,
            label = "Enter name",
            keyboardType = KeyboardType.Text,
            isValid = isValidName,
            errorMessage = "Name can't be empty",
        )

        OutlinedTextFieldWithError(
            value = email,
            onValueChange = onEmailChange,
            label = "Enter email",
            keyboardType = KeyboardType.Email,
            isValid = isValidEmail,
            errorMessage = "Invalid email format",
        )

        OutlinedTextFieldWithError(
            value = numColors,
            onValueChange = onNumColorsChange,
            label = "Enter number of colors",
            keyboardType = KeyboardType.Number,
            isValid = isValidNumColors,
            errorMessage = "Must be between 5 and 10",
        )

        Spacer(modifier = Modifier.height(32.dp))

        val isFormValid = isValidName(name) && isValidEmail(email) && isValidNumColors(numColors)

        Button(
            onClick = { onNavigateToGame(numColors.toInt()) },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Start game")
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Stan pól ProfileScreen, podniesiony do NavHost (State Hoisting)
    val nameState = rememberSaveable { mutableStateOf("") }
    val emailState = rememberSaveable { mutableStateOf("") }
    val numColorsState = rememberSaveable { mutableStateOf("5") }
    val profileImageUriState = rememberSaveable { mutableStateOf<Uri?>(null) }


    NavHost(
        navController = navController,
        startDestination = Destinations.PROFILE_ROUTE,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(
            route = Destinations.PROFILE_ROUTE,
            enterTransition = {
                fadeIn(animationSpec = tween(500, easing = LinearEasing)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(500, easing = EaseIn)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500, easing = LinearEasing)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(500, easing = EaseOut)
                        )
            }
        ) {
            ProfileScreen(
                name = nameState.value,
                email = emailState.value,
                numColors = numColorsState.value,
                profileImageUri = profileImageUriState.value,
                onNameChange = { nameState.value = it },
                onEmailChange = { emailState.value = it },
                onNumColorsChange = { numColorsState.value = it },
                onImageSelect = { profileImageUriState.value = it },
                onNavigateToGame = { numColors ->
                    navController.navigate(
                        Destinations.GAME_ROUTE.replace(
                            "{${Destinations.NUM_COLORS_KEY}}",
                            numColors.toString()
                        )
                    )
                }
            )
        }

        // Ekran Gry
        composable(
            route = Destinations.GAME_ROUTE,
            arguments = listOf(
                navArgument(Destinations.NUM_COLORS_KEY) { type = NavType.IntType }
            ),
            enterTransition = {
                fadeIn(animationSpec = tween(500, easing = LinearEasing)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(500, easing = EaseIn)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500, easing = LinearEasing)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(500, easing = EaseOut)
                        )
            }
        ) { backStackEntry ->
            val numColors = backStackEntry.arguments?.getInt(Destinations.NUM_COLORS_KEY) ?: 5

            GameScreen(
                numAvailableColors = numColors,
                onNavigateToResults = { score ->
                    navController.navigate(
                        Destinations.RESULTS_ROUTE.replace(
                            "{${Destinations.SCORE_KEY}}",
                            score.toString()
                        )
                    )
                },
                onNavigateBackToProfile = {
                    // Wylogowanie/Przerwanie gry (Lab 2c - #5)
                    nameState.value = ""
                    emailState.value = ""
                    numColorsState.value = "5"
                    profileImageUriState.value = null

                    navController.popBackStack(Destinations.PROFILE_ROUTE, inclusive = false)
                }
            )
        }

        // Ekran Wyników
        composable(
            route = Destinations.RESULTS_ROUTE,
            arguments = listOf(
                navArgument(Destinations.SCORE_KEY) { type = NavType.IntType }
            ),
            enterTransition = {
                fadeIn(animationSpec = tween(500, easing = LinearEasing)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(500, easing = EaseIn)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500, easing = LinearEasing)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(500, easing = EaseOut)
                        )
            }
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt(Destinations.SCORE_KEY) ?: 0

            ResultsScreen(
                recentScore = score,
                onRestartGame = {
                    // Powrót do GameScreen (Lab 2c - #3)
                    navController.popBackStack()
                },
                onLogout = {
                    // Wylogowanie i powrót do ProfileScreen (Lab 2c - #4)
                    nameState.value = ""
                    emailState.value = ""
                    numColorsState.value = "5"
                    profileImageUriState.value = null

                    navController.popBackStack(Destinations.PROFILE_ROUTE, inclusive = false)
                }
            )
        }
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
                    AppNavigation() // Używamy zrefaktoryzowanej nawigacji
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MjKappTheme {
        ProfileScreen(
            name = "Test",
            email = "a@b.com",
            numColors = "5",
            profileImageUri = null,
            onNameChange = {},
            onEmailChange = {},
            onNumColorsChange = {},
            onImageSelect = {},
            onNavigateToGame = {}
        )
    }
}