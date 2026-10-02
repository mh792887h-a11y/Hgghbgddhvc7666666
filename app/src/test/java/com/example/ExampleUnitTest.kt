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
    fun appIdentityTest() {
        val appName = "ملاحظاتي الاحترافية"
        val author = "محمد هشام الصلاحي"
        assertTrue(appName.isNotBlank())
        assertTrue(author.isNotBlank())
        assertEquals("ملاحظاتي الاحترافية", appName)
    }
}
