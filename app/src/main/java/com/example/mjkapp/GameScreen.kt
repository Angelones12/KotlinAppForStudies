package com.example.mjkapp

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

@Composable
fun CircularButton(
    onClick: () -> Unit,
    color: Color,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = MaterialTheme.colorScheme.onBackground
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

@Composable
fun FeedbackCircles(
    feedbackColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = 5.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SmallCircle(feedbackColors.getOrElse(0) { MaterialTheme.colorScheme.background }, size = 10.dp)
            SmallCircle(feedbackColors.getOrElse(1) { MaterialTheme.colorScheme.background }, size = 10.dp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SmallCircle(feedbackColors.getOrElse(2) { MaterialTheme.colorScheme.background }, size = 10.dp)
            SmallCircle(feedbackColors.getOrElse(3) { MaterialTheme.colorScheme.background }, size = 10.dp)
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

        Spacer(modifier = Modifier.width(16.dp))

        FeedbackCircles(
            feedbackColors = feedbackColors
        )
    }
}

@Composable
fun GameScreen(
    onNavigateBack: () -> Unit
) {
    val unfoundColor = MaterialTheme.colorScheme.background
    var correctColors by remember { mutableStateOf(selectRandomColors(AvailableColors)) }
    var attempts by remember { mutableStateOf<SnapshotStateList<GameAttempt>>(mutableStateListOf()) }
    var currentGuess by remember { mutableStateOf(List(4) { Color.Transparent }) }
    var isGameWon by remember { mutableStateOf(false) }

    val rowData = attempts + listOfNotNull(if (!isGameWon) GameAttempt(currentGuess, emptyList()) else null)
    val currentAttemptIndex = rowData.lastIndex
    val score = attempts.size

    val onSelectColorClick: (Int) -> Unit = { buttonIndex ->
        if (!isGameWon) {
            currentGuess = currentGuess.toMutableList().apply {
                this[buttonIndex] = selectNextAvailableColor(
                    availableColors = AvailableColors,
                    selectedColors = currentGuess,
                    buttonIndex = buttonIndex
                )
            }
        }
    }

    val onCheckClick: () -> Unit = {
        if (currentGuess.none { it == Color.Transparent }) {
            val feedback = checkColors(currentGuess, correctColors, unfoundColor)
            val newAttempt = GameAttempt(currentGuess, feedback)
            attempts.add(newAttempt)
            currentGuess = List(4) { Color.Transparent }

            if (feedback.count { it == Color.Red } == 4) {
                isGameWon = true
            }
        }
    }

    val onStartOverClick: () -> Unit = {
        correctColors = selectRandomColors(AvailableColors)
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
        Text(
            text = "Your score: $score",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(rowData) { index, attempt ->
                GameRow(
                    selectedColors = attempt.guess,
                    feedbackColors = attempt.feedback,
                    clickable = index == currentAttemptIndex && !isGameWon,
                    onSelectColorClick = onSelectColorClick,
                    onCheckClick = onCheckClick
                )
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