package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class NoteSnapshot(
    val timestamp: Long,
    val title: String,
    val content: String,
    val checklistJson: String = ""
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("timestamp", timestamp)
        json.put("title", title)
        json.put("content", content)
        json.put("checklistJson", checklistJson)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): NoteSnapshot {
            return NoteSnapshot(
                timestamp = json.optLong("timestamp", System.currentTimeMillis()),
                title = json.optString("title", ""),
                content = json.optString("content", ""),
                checklistJson = json.optString("checklistJson", "")
            )
        }

        fun listToJson(snapshots: List<NoteSnapshot>): String {
            val arr = JSONArray()
            for (snap in snapshots) {
                arr.put(snap.toJson())
            }
            return arr.toString()
        }

        fun listFromJson(jsonStr: String): List<NoteSnapshot> {
            if (jsonStr.isBlank()) return emptyList()
            val list = mutableListOf<NoteSnapshot>()
            try {
                val arr = JSONArray(jsonStr)
                for (i in 0 until arr.length()) {
                    list.add(fromJson(arr.getJSONObject(i)))
                }
            } catch (e: Exception) {
                // Return empty if parsing error
            }
            return list
        }
    }
}
