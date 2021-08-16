package com.github.mennovogel.morph

internal object ScaleCalculator {

    /**
     * Calculate the scale during an animation.
     */
    internal fun calculateScale(
        startSize: Float,
        endSize: Float,
        animationProgress: Float,
        animateToTarget: Boolean
    ): Float {
        return if (animateToTarget) {
            val startScale = startSize / endSize
            startScale + (1F - startScale) * animationProgress
        } else {
            val endScale = endSize / startSize
            endScale - (endScale - 1) * animationProgress
        }
    }
}