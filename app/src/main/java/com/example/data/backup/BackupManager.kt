package com.example.data.backup

import android.content.Context
import android.net.Uri
import com.example.data.model.Note
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {

    private const val AUTO_BACKUP_FILENAME = "pronotes_auto_backup.json"

    fun exportNotesToJson(notes: List<Note>): String {
        val root = JSONObject()
        root.put("app", "ProNotes")
        root.put("version", 2)
        root.put("exportTime", System.currentTimeMillis())
        root.put("exportDateFormatted", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        root.put("totalNotes", notes.size)

        val array = JSONArray()
        for (note in notes) {
            array.put(note.toJson())
        }
        root.put("notes", array)
        return root.toString(2)
    }

    fun parseNotesFromJson(jsonStr: String): List<Note> {
        val notes = mutableListOf<Note>()
        try {
            val root = JSONObject(jsonStr)
            val array = root.optJSONArray("notes")
            if (array != null) {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    notes.add(Note.fromJson(obj))
                }
            }
        } catch (e: Exception) {
            // Check if it was a raw JSON array
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    notes.add(Note.fromJson(obj))
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return notes
    }

    fun saveAutoBackup(context: Context, notes: List<Note>): Boolean {
        return try {
            val json = exportNotesToJson(notes)
            val file = File(context.filesDir, AUTO_BACKUP_FILENAME)
            FileOutputStream(file).use { out ->
                out.write(json.toByteArray(Charsets.UTF_8))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAutoBackupInfo(context: Context): Pair<Int, Long>? {
        val file = File(context.filesDir, AUTO_BACKUP_FILENAME)
        if (!file.exists()) return null
        return try {
            val text = file.readText(Charsets.UTF_8)
            val root = JSONObject(text)
            val count = root.optInt("totalNotes", 0)
            val time = root.optLong("exportTime", file.lastModified())
            Pair(count, time)
        } catch (e: Exception) {
            null
        }
    }

    fun restoreAutoBackup(context: Context): List<Note> {
        val file = File(context.filesDir, AUTO_BACKUP_FILENAME)
        if (!file.exists()) return emptyList()
        return try {
            val text = file.readText(Charsets.UTF_8)
            parseNotesFromJson(text)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
