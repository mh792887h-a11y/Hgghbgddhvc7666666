package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.backup.BackupManager
import com.example.data.model.ChecklistItem
import com.example.data.model.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ملاحظاتي الاحترافية", appName)
    }

    @Test
    fun testNoteModelWordCountAndChecklists() {
        val note = Note(
            id = 1L,
            title = "تجربة ملاحظة",
            content = "هذا نص تجريبي للاختبار",
            category = "عمل"
        )
        assertTrue(note.getWordCount() > 0)
        assertEquals("عمل", note.category)
        assertFalse(note.isPinned)
    }

    @Test
    fun testChecklistSerialization() {
        val items = listOf(
            ChecklistItem(text = "شراء حليب", isChecked = false),
            ChecklistItem(text = "إنجاز العمل", isChecked = true)
        )
        val json = ChecklistItem.listToJson(items)
        val parsed = ChecklistItem.listFromJson(json)

        assertEquals(2, parsed.size)
        assertEquals("شراء حليب", parsed[0].text)
        assertFalse(parsed[0].isChecked)
        assertTrue(parsed[1].isChecked)
    }

    @Test
    fun testBackupManagerExportAndImport() {
        val note = Note(
            id = 10L,
            title = "ملاحظة نسخ احتياطي",
            content = "محتوى النسخ الاحتياطي",
            category = "أفكار"
        )
        val json = BackupManager.exportNotesToJson(listOf(note))
        val restored = BackupManager.parseNotesFromJson(json)

        assertEquals(1, restored.size)
        assertEquals("ملاحظة نسخ احتياطي", restored[0].title)
        assertEquals("أفكار", restored[0].category)
    }
}
