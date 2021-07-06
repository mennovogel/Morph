package nl.birdly.crossfadedemo

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun AnimatingFab(modifier: Modifier) {
    var state by remember { mutableStateOf(State.FAB) }

    val animationDuration = 200

    val clickAction: () -> Unit = {
        state = when (state) {
            State.FAB -> State.LIST
            State.LIST -> State.FAB
        }
    }

    SizeAnimation(
        modifier = modifier,
        targetState = state,
        animationSpec = tween(animationDuration)
    ) { sizeState ->
        when (sizeState) {
            State.FAB -> {
                FloatingActionButton(
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    backgroundColor = MaterialTheme.colors.secondary,
                    onClick = clickAction
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_add_24),
                        contentScale = ContentScale.Inside,
                        contentDescription = "Add"
                    )
                }
            }
            State.LIST -> {
                Card(
                    elevation = 4.dp,
                    backgroundColor = MaterialTheme.colors.background
                ) {
                    val listItems = listOf("Option 1", "Option 2", "Option 3")

                    LazyColumn(
                        modifier = Modifier
                            .width(200.dp)
                    ) {
                        items(listItems, itemContent = { item ->
                            Box(
                                modifier
                                    .clickable { clickAction() }
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    item, modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(16.dp)
                                )
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun Fab(modifier: Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        elevation = FloatingActionButtonDefaults.elevation(4.dp),
        backgroundColor = MaterialTheme.colors.secondary,
        modifier = modifier
            .width(56.dp)
            .height(56.dp),
        onClick = onClick
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_add_24),
            contentScale = ContentScale.Inside,
            contentDescription = "Add"
        )
    }
}

@Composable
private fun List(modifier: Modifier, onClick: () -> Unit) {
    Card(
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.background,
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
    ) {
        val listItems = listOf("Option 1", "Option 2", "Option 3")

        LazyColumn(modifier = Modifier
            .width(200.dp)
        ) {
            items(listItems, itemContent = { item ->
                Box(
                    modifier
                        .clickable(onClick = onClick)
                        .fillMaxWidth()
                ) {
                    Text(item, modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                    )
                }
            })
        }
    }
}

private enum class State {
    FAB, LIST
}