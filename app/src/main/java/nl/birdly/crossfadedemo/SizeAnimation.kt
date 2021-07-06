package nl.birdly.crossfadedemo

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout

@Composable
fun SizeAnimation(
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = spring(),
    targetState: SizeState,
    content: @Composable (SizeState) -> Unit
) {
    var minSize by remember { mutableStateOf<Size?>(null) }
    var maxSize by remember { mutableStateOf<Size?>(null) }

    val items = remember { mutableStateListOf<SizeAnimationItem>() }
    val transitionState = remember { MutableTransitionState(targetState) }
    val targetChanged = (targetState != transitionState.targetState)
    transitionState.targetState = targetState
    val transition = updateTransition(transitionState, label = "transition")

    if (targetChanged || items.isEmpty()) {
        // Only manipulate the list when the state is changed, or in the first run.
        val keys = items.map { it.key }.run {
            if (!contains(targetState)) {
                toMutableList().also { it.add(targetState) }
            } else {
                this
            }
        }
        items.clear()
        keys.mapTo(items) { key ->
            SizeAnimationItem(key) {
                val alpha by transition.animateFloat(
                    transitionSpec = { animationSpec }, label = "alpha"
                ) { if (it == key) 1f else 0f }
                Box(Modifier.alpha(alpha = alpha)) {
                    content(key)
                }
            }
        }
    } else if (transitionState.currentState == transitionState.targetState) {
        // Remove all the intermediate items from the list once the animation is finished.
        items.removeAll { it.key != transitionState.targetState }
    }

    Box(modifier) {
        items.forEach { sizeAnimationItem ->
            key(sizeAnimationItem.key) {

                val animationProgress by transition.animateFloat(
                    transitionSpec = { animationSpec }, label = "animationProgress"
                ) { target -> if (target == sizeAnimationItem.key) 1f else 0f }

                Layout(
                    modifier = Modifier,
                    content = sizeAnimationItem.content
                ) { measurables, constraints ->
                    // Don't constrain child views further, measure them with given constraints
                    // List of measured children
                    val placeables = measurables.map { measurable ->
                        // Measure each children
                        measurable.measure(constraints)
                    }

                    val currentWidth: Int = placeables.map { it.width }.maxOrNull() ?: 0
                    val currentHeight = placeables.map { it.height }.maxOrNull() ?: 0

                    if (minSize == null || currentWidth < minSize?.width!!) {
                        minSize = Size(currentWidth.toFloat(), currentHeight.toFloat())
                    }

                    if (maxSize == null || currentWidth > maxSize?.width!!) {
                        maxSize = Size(currentWidth.toFloat(), currentHeight.toFloat())
                    }

                    // Set the size of the layout as big as it can
                    layout(
                        constraints.minWidth,
                        constraints.minHeight
                    ) {
                        // Place children in the parent layout
                        placeables.forEach { placeable ->
                            // Position item on the screen
                            placeable.placeRelativeWithLayer(
                                x = -placeable.width,
                                y = -placeable.height,
                            ) {
                                transformOrigin = TransformOrigin(
                                    1f,
                                    1f
                                )

                                // minSize and maxSize cannot be null at this point
                                val immutableMinSize = minSize ?: return@placeRelativeWithLayer
                                val immutableMaxSize = maxSize ?: return@placeRelativeWithLayer
                                val startSize = if (targetState == SizeState.START) {
                                    immutableMaxSize
                                } else {
                                    immutableMinSize
                                }
                                val endSize = if (targetState == SizeState.START) {
                                    immutableMinSize
                                } else {
                                    immutableMaxSize
                                }

                                scaleX = calculateScale(
                                    startSize.width,
                                    endSize.width,
                                    animationProgress,
                                    targetState == sizeAnimationItem.key
                                )

                                scaleY = calculateScale(
                                    startSize.height,
                                    endSize.height,
                                    animationProgress,
                                    targetState == sizeAnimationItem.key
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun calculateScale(
    startSize: Float,
    endSize: Float,
    progress: Float,
    animateToTarget: Boolean
): Float {
    return if (animateToTarget) {
        val startScale = startSize / endSize
        startScale + (1F - startScale) * progress
    } else {
        val endScale = endSize / startSize
        endScale - (endScale - 1) * progress
    }
}

enum class SizeState {
    START, END
}

private data class SizeAnimationItem(
    val key: SizeState,
    val content: @Composable () -> Unit
)