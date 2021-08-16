package com.github.mennovogel.morph

import org.junit.Assert.assertEquals
import org.junit.Test

class ScaleCalculatorTest {

    @Test
    fun `calculateScale with animateFromTarget starts at startSize`() {
        assertEquals(0.5f, ScaleCalculator.calculateScale(
            1f, 2f, 0f, true
        ))
    }

    @Test
    fun `calculateScale with animateFromTarget ends at 1`() {
        assertEquals(1f, ScaleCalculator.calculateScale(
            1f, 2f, 1f, true
        ))
    }

    @Test
    fun `calculateScale with animateFromTarget halfway is halfway`() {
        assertEquals(0.75f, ScaleCalculator.calculateScale(
            1f, 2f, 0.5f, true
        ))
    }

    @Test
    fun `calculateScale without animateFromTarget starts at startSize`() {
        assertEquals(2f, ScaleCalculator.calculateScale(
            1f, 2f, 0f, false
        ))
    }

    @Test
    fun `calculateScale without animateFromTarget ends at 1`() {
        assertEquals(1f, ScaleCalculator.calculateScale(
            1f, 2f, 1f, false
        ))
    }

    @Test
    fun `calculateScale without animateFromTarget halfway is halfway`() {
        assertEquals(1.5f, ScaleCalculator.calculateScale(
            1f, 2f, 0.5f, false
        ))
    }
}