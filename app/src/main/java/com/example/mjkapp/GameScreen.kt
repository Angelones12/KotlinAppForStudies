// angelones12/kotlinappforstudies/KotlinAppForStudies-b96ec3468917a2671c5912202db414b554475358/app/src/main/java/com/example/mjkapp/GameScreen.kt
package com.example.mjkapp

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Dodatkowe importy dla animacji
import androidx.compose.animation.*
import androidx.compose.animation.core.*

@Composable
fun CircularButton(
    onClick: () -> Unit,
    color: Color,
    enabled: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = repeatable( // Powtarza się kilkukrotnie (np. 3 razy)
            iterations = 3,
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonColorAnimation"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedColor,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = animatedColor,
            disabledContentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.size(50.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        content()
    }
}

@Composable
fun SmallCircle(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 10.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
    )
}

// FeedbackCircles z sekwencyjną animacją koloru
@Composable
fun FeedbackCircles(
    feedbackColors: List<Color>,
    modifier: Modifier = Modifier
) {
    // Używamy Color.Red jako domyślnego koloru "pudła" (mismatch)
    val initialColor = Color.Red

    val color1 = remember { Animatable(initialColor) }
    val color2 = remember { Animatable(initialColor) }
    val color3 = remember { Animatable(initialColor) }
    val color4 = remember { Animatable(initialColor) }

    // Uruchamiamy sekwencyjną animację przy zmianie listy feedbackColors
    LaunchedEffect(feedbackColors) {
        val colors = listOf(color1, color2, color3, color4)
        // Jeśli lista jest pusta (np. dla bieżącej próby), ustawiamy domyślny kolor (Red)
        val targetColors = if (feedbackColors.isNotEmpty()) {
            feedbackColors
        } else {
            List(4) { initialColor }
        }

        // Sekwencyjna animacja
        for (i in 0..3) {
            colors[i].animateTo(targetColors[i], animationSpec = tween(150))
        }
    }

    Column(
        modifier = modifier.padding(end = 5.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SmallCircle(color1.value, size = 10.dp)
            SmallCircle(color2.value, size = 10.dp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SmallCircle(color3.value, size = 10.dp)
            SmallCircle(color4.value, size = 10.dp)
        }
    }
}

@Composable
fun SelectableColorsRow(
    colors: List<Color>,
    onClick: (Int) -> Unit,
    clickable: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            CircularButton(
                onClick = { onClick(index) },
                color = colors.getOrElse(index) { MaterialTheme.colorScheme.surface },
                enabled = clickable
            )
        }
    }
}

@Composable
fun GameRow(
    selectedColors: List<Color>,
    feedbackColors: List<Color>,
    clickable: Boolean,
    onSelectColorClick: (Int) -> Unit,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allColorsSelected = selectedColors.none { it == Color.Transparent }
    val checkEnabled = clickable && allColorsSelected

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectableColorsRow(
            colors = selectedColors,
            onClick = onSelectColorClick,
            clickable = clickable,
            modifier = Modifier.weight(1f)
        )

        // Przycisk zatwierdzający z animacją widoczności
        AnimatedVisibility(
            visible = clickable,
            enter = scaleIn(
                animationSpec = tween(300),
                initialScale = 0f
            ),
            exit = scaleOut(
                animationSpec = tween(300),
                targetScale = 0f
            )
        ) {
            IconButton(
                onClick = onCheckClick,
                enabled = checkEnabled,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Zatwierdź próbę",
                    tint = if (checkEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }


        Spacer(modifier = Modifier.width(16.dp))

        FeedbackCircles(
            feedbackColors = feedbackColors
        )
    }
}

@Composable
fun GameScreen(
    numAvailableColors: Int,
    onNavigateToResults: (score: Int) -> Unit,
    onNavigateBackToProfile: () -> Unit
) {
    var correctColors by remember(numAvailableColors) { mutableStateOf(selectRandomColors(AvailableColors, numAvailableColors)) }
    var attempts by remember(numAvailableColors) { mutableStateOf<SnapshotStateList<GameAttempt>>(mutableStateListOf()) }
    var currentGuess by remember(numAvailableColors) { mutableStateOf(List(4) { Color.Transparent }) }
    var isGameWon by remember(numAvailableColors) { mutableStateOf(false) }
    val rowData = attempts + listOfNotNull(if (!isGameWon) GameAttempt(currentGuess, emptyList()) else null)
    val currentAttemptIndex = rowData.lastIndex
    val score = attempts.size
    val selectionPool = remember(numAvailableColors) {
        AvailableColors.take(numAvailableColors)
    }

    val onSelectColorClick: (Int) -> Unit = { buttonIndex ->
        if (!isGameWon) {
            currentGuess = currentGuess.toMutableList().apply {
                this[buttonIndex] = selectNextAvailableColor(
                    availableColors = selectionPool,
                    selectedColors = currentGuess,
                    buttonIndex = buttonIndex
                )
            }
        }
    }

    val onCheckClick: () -> Unit = {
        if (currentGuess.none { it == Color.Transparent }) {
            // Wywołujemy logikę sprawdzania (Green/Yellow/Red)
            val feedback = checkColors(currentGuess, correctColors)
            val newAttempt = GameAttempt(currentGuess, feedback)
            attempts.add(newAttempt)
            currentGuess = List(4) { Color.Transparent }

            // Sprawdzamy wygraną (4 ZIELONE kółka)
            if (feedback.count { it == Color.Green } == 4) {
                isGameWon = true
                onNavigateToResults(attempts.size)
            }
        }
    }

    val onStartOverClick: () -> Unit = {
        correctColors = selectRandomColors(AvailableColors, numAvailableColors)
        attempts = mutableStateListOf()
        currentGuess = List(4) { Color.Transparent }
        isGameWon = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your score: $score",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onNavigateBackToProfile) {
                Text("Logout")
            }
        }


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(rowData) { index, attempt ->
                AnimatedVisibility(
                    visible = true,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween(500)
                    ) + fadeIn(tween(500)),
                    exit = fadeOut(tween(500))
                ) {
                    GameRow(
                        selectedColors = attempt.guess,
                        feedbackColors = attempt.feedback,
                        // Można klikać tylko wiersz, który jest aktualną próbą
                        clickable = index == currentAttemptIndex && !isGameWon,
                        onSelectColorClick = onSelectColorClick,
                        onCheckClick = onCheckClick
                    )
                }

                if (index < rowData.size - 1) {
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }

        if (isGameWon) {
            Button(
                onClick = onStartOverClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp)
            ) {
                Text("Start over")
            }
        }
    }
}