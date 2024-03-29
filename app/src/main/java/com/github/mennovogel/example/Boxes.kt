package com.github.mennovogel.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.mennovogel.morph.Morph
import nl.birdly.example.R

@Preview
@Composable
fun Boxes(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(BoxState.LARGE) }

    Morph(
        targetState = state,
        modifier = modifier,
        contentAlignment = Alignment.Center,
        fadePreviousState = false,
    ) { crossFadeState ->
        when (crossFadeState) {
            BoxState.LARGE -> {
                Box(modifier = Modifier
                    .size(300.dp)
                    .background(color = MaterialTheme.colors.primary)
                    .clickable {
                        state = BoxState.SMALL
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.hello_purple_box),
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            BoxState.SMALL -> {
                Box(modifier = Modifier
                    .size(100.dp)
                    .background(color = MaterialTheme.colors.secondary)
                    .clickable {
                        state = BoxState.LARGE
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.hey_green_box),
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

private enum class BoxState {
    LARGE, SMALL
}