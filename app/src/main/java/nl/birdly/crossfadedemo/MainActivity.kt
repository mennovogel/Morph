package nl.birdly.crossfadedemo

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }

    // Start building your app here!
    @Composable
    fun MyApp() {
        Surface(color = MaterialTheme.colors.background) {
            ConstraintLayout(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
            ) {
                val (boxes, fab) = createRefs()

                Boxes(Modifier.constrainAs(boxes) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                })
                AnimatingFab(Modifier.constrainAs(fab) {
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                })
            }
        }
    }

    @Composable
    private fun Boxes(modifier: Modifier) {
        var state by remember { mutableStateOf(State.START) }

        SizeAnimation(
            targetState = state,
            modifier = modifier,
            animationSpec = tween(2000),
        ) { crossFadeState ->
            when (crossFadeState) {
                State.START -> {
                    Box(modifier = Modifier
                        .size(300.dp)
                        .background(color = MaterialTheme.colors.primary)
                        .clickable {
                            state = State.END
                        }
                    )
                }
                State.END -> {
                    Box(modifier = Modifier
                        .size(100.dp)
                        .background(color = MaterialTheme.colors.secondary)
                        .clickable {
                            state = State.START
                        }
                    )
                }
            }
        }
    }

    private enum class State {
        START, END
    }
}