package com.example.lab10numberguessing

import GuessResult
import NumberGame
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab10numberguessing.ui.theme.AndroidsensorlabsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidsensorlabsTheme {
                NumberGuessingGameApp()
            }
        }
    }
}

@Composable
fun NumberGuessingGameApp() {
    val numberGame = remember { NumberGame(1..10) }  // Create a NumberGame instance
    NumberGuessingGameButtonAndText(numberGame, modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center))
}

@Composable
fun NumberGuessingGameButtonAndText(numberGame: NumberGame, modifier: Modifier = Modifier) {
    var guessInput by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<GuessResult?>(null) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Guess a number in 1..10")
        OutlinedTextField(
            value = guessInput,
            onValueChange = { guessInput = it },
            label = { Text("Enter your guess") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

        )
        Button(onClick = {
            val guess = guessInput.toIntOrNull()
            if (guess != null) {
                result = numberGame.makeGuess(guess)
            }
        }) {
            Text("Submit Guess")
        }
        result?.let {
            Text(
                text = when (it) {
                    GuessResult.HIGH -> "Too High!"
                    GuessResult.LOW -> "Too Low!"
                    GuessResult.HIT -> "Correct!"
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewNumberGuessingGameApp() {
    AndroidsensorlabsTheme {
        NumberGuessingGameApp()
    }
}


