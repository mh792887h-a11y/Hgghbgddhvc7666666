package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun testCoreConstants() {
        val appTitle = "ملاحظاتي الاحترافية"
        val developer = "محمد هشام الصلاحي"
        assertNotNull(appTitle)
        assertNotNull(developer)
        assertTrue(appTitle.contains("ملاحظاتي"))
        assertEquals("محمد هشام الصلاحي", developer)
    }
}
