package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun project_budget_and_balance_calculation() {
        val budget = 750.0
        val agreedPrice = 700.0
        val paidAmount = 350.0
        val outstanding = (agreedPrice - paidAmount)

        assertEquals(350.0, outstanding, 0.001)
        assertTrue(outstanding > 0)
    }

    @Test
    fun project_code_format_validation() {
        val code = "FA-8492"
        assertTrue(code.startsWith("FA-"))
        assertEquals(7, code.length)
    }
}
