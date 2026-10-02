package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val category: String = "عام",
    val colorHex: String = "#00000000",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isTrashed: Boolean = false,
    val isFavorite: Boolean = false,
    val isLocked: Boolean = false,
    val lockPin: String = "",
    val isChecklist: Boolean = false,
    val checklistJson: String = "",
    val drawingData: String = "",
    val audioPath: String = "",
    val audioDurationMs: Long = 0L,
    val reminderTimestamp: Long? = null,
    val tags: String = "", // Comma separated tags e.g. "مهم, مشروع"
    val historyJson: String = "", // JSON of NoteSnapshot list
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun getChecklistItems(): List<ChecklistItem> {
        return ChecklistItem.listFromJson(checklistJson)
    }

    fun getDrawingStrokes(): List<DrawingStroke> {
        return DrawingStroke.listFromJson(drawingData)
    }

    fun getHistorySnapshots(): List<NoteSnapshot> {
        return NoteSnapshot.listFromJson(historyJson)
    }

    fun getTagsList(): List<String> {
        if (tags.isBlank()) return emptyList()
        return tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }

    fun getWordCount(): Int {
        val text = if (isChecklist) {
            getChecklistItems().joinToString(" ") { it.text }
        } else {
            "$title $content"
        }
        return text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
    }

    fun getCharacterCount(): Int {
        val text = if (isChecklist) {
            getChecklistItems().joinToString(" ") { it.text }
        } else {
            content
        }
        return text.length
    }

    fun getCompletedChecklistCount(): Pair<Int, Int> {
        val items = getChecklistItems()
        val completed = items.count { it.isChecked }
        return Pair(completed, items.size)
    }

    fun hasDrawing(): Boolean = drawingData.isNotBlank() && drawingData != "[]"

    fun hasAudio(): Boolean = audioPath.isNotBlank()

    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("title", title)
        obj.put("content", content)
        obj.put("category", category)
        obj.put("colorHex", colorHex)
        obj.put("isPinned", isPinned)
        obj.put("isArchived", isArchived)
        obj.put("isFavorite", isFavorite)
        obj.put("isLocked", isLocked)
        obj.put("lockPin", lockPin)
        obj.put("isChecklist", isChecklist)
        obj.put("checklistJson", checklistJson)
        obj.put("drawingData", drawingData)
        obj.put("audioPath", audioPath)
        obj.put("audioDurationMs", audioDurationMs)
        obj.put("tags", tags)
        obj.put("createdAt", createdAt)
        obj.put("updatedAt", updatedAt)
        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): Note {
            return Note(
                id = obj.optLong("id", 0L),
                title = obj.optString("title", ""),
                content = obj.optString("content", ""),
                category = obj.optString("category", "عام"),
                colorHex = obj.optString("colorHex", "#00000000"),
                isPinned = obj.optBoolean("isPinned", false),
                isArchived = obj.optBoolean("isArchived", false),
                isFavorite = obj.optBoolean("isFavorite", false),
                isLocked = obj.optBoolean("isLocked", false),
                lockPin = obj.optString("lockPin", ""),
                isChecklist = obj.optBoolean("isChecklist", false),
                checklistJson = obj.optString("checklistJson", ""),
                drawingData = obj.optString("drawingData", ""),
                audioPath = obj.optString("audioPath", ""),
                audioDurationMs = obj.optLong("audioDurationMs", 0L),
                tags = obj.optString("tags", ""),
                createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
            )
        }
    }
}
