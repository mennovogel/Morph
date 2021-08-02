package nl.birdly.example

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.birdly.morph.Morph

@Preview
@Composable
fun Boxes(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(BoxesState.START) }

    Morph(
        targetState = state,
        modifier = modifier,
        contentAlignment = Alignment.Center,
        keepOldStateVisible = true,
    ) { crossFadeState ->
        when (crossFadeState) {
            BoxesState.START -> {
                Box(modifier = Modifier
                    .size(300.dp)
                    .background(color = MaterialTheme.colors.primary)
                    .clickable {
                        state = BoxesState.END
                    }
                )
            }
            BoxesState.END -> {
                Box(modifier = Modifier
                    .size(100.dp)
                    .background(color = MaterialTheme.colors.secondary)
                    .clickable {
                        state = BoxesState.START
                    }
                )
            }
        }
    }
}

private enum class BoxesState {
    START, END
}