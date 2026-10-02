package com.example.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import org.json.JSONArray
import org.json.JSONObject

data class DrawingPoint(
    val x: Float,
    val y: Float
)

data class DrawingStroke(
    val points: List<DrawingPoint>,
    val colorHex: String,
    val strokeWidth: Float
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("color", colorHex)
        obj.put("width", strokeWidth.toDouble())
        val pointsArr = JSONArray()
        for (p in points) {
            val pObj = JSONObject()
            pObj.put("x", p.x.toDouble())
            pObj.put("y", p.y.toDouble())
            pointsArr.put(pObj)
        }
        obj.put("points", pointsArr)
        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): DrawingStroke {
            val color = obj.optString("color", "#000000")
            val width = obj.optDouble("width", 5.0).toFloat()
            val points = mutableListOf<DrawingPoint>()
            val pointsArr = obj.optJSONArray("points")
            if (pointsArr != null) {
                for (i in 0 until pointsArr.length()) {
                    val pObj = pointsArr.getJSONObject(i)
                    points.add(
                        DrawingPoint(
                            x = pObj.optDouble("x", 0.0).toFloat(),
                            y = pObj.optDouble("y", 0.0).toFloat()
                        )
                    )
                }
            }
            return DrawingStroke(points, color, width)
        }

        fun listToJson(strokes: List<DrawingStroke>): String {
            val arr = JSONArray()
            for (stroke in strokes) {
                arr.put(stroke.toJson())
            }
            return arr.toString()
        }

        fun listFromJson(jsonStr: String): List<DrawingStroke> {
            if (jsonStr.isBlank()) return emptyList()
            val list = mutableListOf<DrawingStroke>()
            try {
                val arr = JSONArray(jsonStr)
                for (i in 0 until arr.length()) {
                    list.add(fromJson(arr.getJSONObject(i)))
                }
            } catch (e: Exception) {
                // Ignore parse errors
            }
            return list
        }
    }
}
