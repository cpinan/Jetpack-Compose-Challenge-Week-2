/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO Make it dynamic in the future
private const val TIME = 30

private const val START_SCREEN = 1
private const val IN_PROGRESS = 2
private const val END_SCREEN = 3

private val currentScreen = mutableStateOf(START_SCREEN)

private var progressState by mutableStateOf(false)
private var progressAlpha by mutableStateOf(1F)

private var timer by mutableStateOf(TIME)

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                CountDownScreen()
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CountDownScreen() {
    val coroutineScope = rememberCoroutineScope()

    Surface(color = MaterialTheme.colors.background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .weight(0.7f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painterResource(id = R.drawable.sand_clock),
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = progressAlpha
                        }
                )

                Text(
                    text = if (currentScreen.value != END_SCREEN) "$timer" else stringResource(R.string.Finish),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 64.sp
                    ),
                )
            }

            Crossfade(
                targetState = currentScreen.value
            ) {
                when (it) {
                    START_SCREEN -> StartOptions(coroutineScope)
                    IN_PROGRESS -> ProgressOptions(coroutineScope)
                    END_SCREEN -> EndOptions(coroutineScope)
                }
            }
        }
    }
}

private suspend fun createAnimation() {
    progressAlpha = 1.0F
    timer = TIME
    val diff = (1F / TIME.toFloat())
    while (progressState) {
        timer--
        progressAlpha -= diff
        if (timer < 0F) {
            timer = 0
            progressAlpha = 1F
            progressState = false
            currentScreen.value = END_SCREEN
            break
        }
        delay(1000L)
    }
}

@Composable
fun StartOptions(coroutineScope: CoroutineScope) {
    ActionButton(stringResource(R.string.Start), Color.Green) {
        progressState = true
        coroutineScope.launch {
            currentScreen.value = IN_PROGRESS
            createAnimation()
        }
    }
}

@Composable
fun ProgressOptions(coroutineScope: CoroutineScope) {
    ActionButton(stringResource(R.string.Stop), Color.Red) {
        progressState = false
        coroutineScope.launch {
            currentScreen.value = END_SCREEN
        }
    }
}

@Composable
fun EndOptions(coroutineScope: CoroutineScope) {
    timer = TIME
    ActionButton(stringResource(R.string.Restart), Color.Yellow) {
        progressState = true
        coroutineScope.launch {
            currentScreen.value = IN_PROGRESS
            createAnimation()
        }
    }
}

@Composable
fun ActionButton(
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 16.dp
        ),
        modifier = Modifier
            .padding(16.dp)
            .width(180.dp)
            .height(64.dp),
        onClick = onClick
    ) {
        Text(
            text = label.toUpperCase(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            letterSpacing = 6.sp
        )
    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        CountDownScreen()
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        CountDownScreen()
    }
}
