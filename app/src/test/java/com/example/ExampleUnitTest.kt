package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun basicMathAndStringTest() {
        val title = "ملاحظاتي الاحترافية"
        assertTrue(title.isNotBlank())
        assertEquals(19, title.length)
    }
}
