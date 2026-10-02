package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.DrawingStroke

@Composable
fun DrawingPreview(
    strokes: List<DrawingStroke>,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    if (strokes.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (stroke in strokes) {
                if (stroke.points.size > 1) {
                    val path = Path()
                    path.moveTo(stroke.points[0].x * (size.width / 800f).coerceAtMost(1f), stroke.points[0].y * (size.height / 500f).coerceAtMost(1f))
                    for (i in 1 until stroke.points.size) {
                        path.lineTo(stroke.points[i].x * (size.width / 800f).coerceAtMost(1f), stroke.points[i].y * (size.height / 500f).coerceAtMost(1f))
                    }
                    drawPath(
                        path = path,
                        color = try { Color(android.graphics.Color.parseColor(stroke.colorHex)) } catch (e: Exception) { Color.Black },
                        style = Stroke(
                            width = (stroke.strokeWidth * 0.5f).coerceAtLeast(2f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
