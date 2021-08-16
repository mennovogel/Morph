package nl.birdly.morph

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

/**
 * [Morph] allows to switch between layouts with both a crossfade and a change
 * bounds animation.
 *
 * @param targetState - is a key representing your target layout state. Every time you change a
 * key the animation will be triggered. The content called with the old key will be faded out while
 * the content called with the new key will be faded in.
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The default alignment inside the [Morph].
 * @param animationSpec the AnimationSpec to configure the animation.
 * @param keepOldStateVisible Whether to fade out the content with the old key during the
 * animation. Setting this to true will prevent the view to transparent during the animation.
 * Halfway the animation both layers would have 0.5F opacity, resulting in a combined opacity of
 * 0.75F. This is false by default, because it can cause unwanted effects when changing to
 * another shape.
 * @param content The content of the state [T] for the [Morph].
 */
@Composable
fun <T> Morph(
    targetState: T,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    animationSpec: FiniteAnimationSpec<Float> = spring(),
    keepOldStateVisible: Boolean = false,
    content: @Composable (T) -> Unit
) {
    var targetSize by remember { mutableStateOf<IntSize?>(null) }
    var previousSize by remember { mutableStateOf<IntSize?>(null) }

    val items = remember { mutableStateListOf<AnimationItem<T>>() }
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
            AnimationItem(key) {
                val animatedAlpha by transition.animateFloat(
                    transitionSpec = { animationSpec }, label = "alpha"
                ) { if (it == key) 1f else 0f }

                val alpha = if (keepOldStateVisible && key != transitionState.targetState) {
                    1f
                } else {
                    animatedAlpha
                }

                Box(Modifier.alpha(alpha = alpha)) {
                    content(key)
                }
            }
        }
    } else if (transitionState.currentState == transitionState.targetState) {
        // Remove all the intermediate items from the list once the animation is finished.
        items.removeAll { it.key != transitionState.targetState }
    }

    Box(modifier = modifier) {
        var boxSize by remember { mutableStateOf(IntSize(0, 0)) }

        items.forEach { sizeAnimationItem ->
            key(sizeAnimationItem.key) {

                val animationProgress by transition.animateFloat(
                    transitionSpec = { animationSpec }, label = "animationProgress"
                ) { target -> if (target == sizeAnimationItem.key) 1f else 0f }

                Layout(
                    modifier = modifier,
                    content = sizeAnimationItem.content
                ) { measurables: List<Measurable>, constraints: Constraints ->
                    // Don't constrain child views further, measure them with given constraints
                    // List of measured children
                    val placeables = measurables.map { measurable ->
                        // Measure each children
                        measurable.measure(constraints)
                    }

                    val currentSize = IntSize(
                        placeables.map { it.width }.maxOrNull() ?: 0,
                        placeables.map { it.height }.maxOrNull() ?: 0
                    )

                    boxSize = updateBoxSize(boxSize, currentSize, items.size)

                    if (sizeAnimationItem.key == targetState) {
                        targetSize = IntSize(currentSize.width, currentSize.height)
                    } else {
                        previousSize = IntSize(currentSize.width, currentSize.height)
                    }

                    layout(
                        currentSize.width,
                        currentSize.height
                    ) {
                        // Place children in the parent layout
                        placeables.forEach { placeable ->
                            val placeableSize =
                                IntSize(placeable.width, placeable.height)

                            val alignment = contentAlignment.align(
                                IntSize(placeable.width, placeable.height),
                                boxSize,
                                layoutDirection
                            )
                            // Position item on the screen
                            placeable.placeRelativeWithLayer(
                                alignment.x,
                                alignment.y
                            ) {
                                // Do a dummy alignment, use this to calculate the pivot points
                                val pivotAlignment = contentAlignment.align(
                                    IntSize(0, 0),
                                    IntSize(100, 100),
                                    layoutDirection
                                )
                                transformOrigin = TransformOrigin(
                                    pivotAlignment.x / 100F,
                                    pivotAlignment.y / 100F
                                )

                                val startSize = previousSize ?: placeableSize
                                val endSize = targetSize ?: placeableSize

                                scaleX = ScaleCalculator.calculateScale(
                                    startSize.width.toFloat(),
                                    endSize.width.toFloat(),
                                    animationProgress,
                                    targetState == sizeAnimationItem.key
                                )

                                scaleY = ScaleCalculator.calculateScale(
                                    startSize.height.toFloat(),
                                    endSize.height.toFloat(),
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

/**
 * Update the boxSize with the new size, the box size should always have the largest bounds of the
 * child items.
 */
private fun updateBoxSize(
    currentBoxSize: IntSize?,
    itemSize: IntSize,
    itemCount: Int
): IntSize {
    if (currentBoxSize == null || itemCount == 1) {
        return IntSize(itemSize.width, itemSize.height)
    } else {
        var mutableBoxSize = currentBoxSize
        // boxSize cannot be null here
        if (itemSize.width.toFloat() > currentBoxSize.width) {
            mutableBoxSize = IntSize(itemSize.width, currentBoxSize.height)
        }
        if (itemSize.height.toFloat() > currentBoxSize.height) {
            return IntSize(mutableBoxSize.width, itemSize.height)
        }
        return mutableBoxSize
    }
}

private data class AnimationItem<T>(
    val key: T,
    val content: @Composable () -> Unit
)