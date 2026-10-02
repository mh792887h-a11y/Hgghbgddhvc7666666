package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isChecked: Boolean = false
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("text", text)
        json.put("isChecked", isChecked)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): ChecklistItem {
            return ChecklistItem(
                id = json.optString("id", UUID.randomUUID().toString()),
                text = json.optString("text", ""),
                isChecked = json.optBoolean("isChecked", false)
            )
        }

        fun listToJson(items: List<ChecklistItem>): String {
            val jsonArray = JSONArray()
            for (item in items) {
                jsonArray.put(item.toJson())
            }
            return jsonArray.toString()
        }

        fun listFromJson(jsonStr: String): List<ChecklistItem> {
            if (jsonStr.isBlank()) return emptyList()
            val list = mutableListOf<ChecklistItem>()
            try {
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(fromJson(obj))
                }
            } catch (e: Exception) {
                // If it was plain text before, parse line by line
                val lines = jsonStr.lines().filter { it.isNotBlank() }
                for (line in lines) {
                    val isChecked = line.startsWith("[x] ") || line.startsWith("[X] ")
                    val cleanText = if (isChecked || line.startsWith("[ ] ")) line.substring(4) else line
                    list.add(ChecklistItem(text = cleanText, isChecked = isChecked))
                }
            }
            return list
        }
    }
}
