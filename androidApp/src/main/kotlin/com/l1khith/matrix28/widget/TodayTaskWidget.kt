package com.l1khith.matrix28.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.launch

import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDecoration
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.l1khith.matrix28.MainActivity
import com.l1khith.matrix28.R
import com.l1khith.matrix28.data.AppTask
import com.l1khith.matrix28.data.TaskDatabase
import com.l1khith.matrix28.utils.FixedCalendarHelper
import com.l1khith.matrix28.utils.currentTimeMillis

class TodayTaskWidget : GlanceAppWidget() {

    companion object {
        fun updateWidget(context: Context) {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    TodayTaskWidget().updateAll(context)
                } catch (e: Exception) {
                    Log.e("TodayTaskWidget", "Failed to update widget instances", e)
                }
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        val db = TaskDatabase()
        val today = FixedCalendarHelper.fromTimestamp(currentTimeMillis())
        val todayStr = today.toString()
        val monthName = FixedCalendarHelper.getMonthName(today.month)
        val todayTasks = try {
            db.getTasksForDate(todayStr)
        } catch (e: Exception) {
            Log.e("TodayTaskWidget", "Error fetching widget tasks for date $todayStr", e)
            emptyList()
        }

        provideContent {
            GlanceWidgetContent(
                dateText = "$monthName ${today.day}, ${today.year}",
                cycleBadgeText = "Month ${today.month} • Day ${today.day} of 28",
                tasks = todayTasks
            )
        }
    }

    @Composable
    private fun GlanceWidgetContent(
        dateText: String,
        cycleBadgeText: String,
        tasks: List<AppTask>
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .cornerRadius(16.dp)
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            // HEADER ROW: LOGO & APP TITLE & BADGE
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_matrix28_logo),
                        contentDescription = "Matrix 28 Logo",
                        modifier = GlanceModifier.width(20.dp).height(20.dp)
                    )

                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = "MATRIX 28",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF3B82F6)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                Box(
                    modifier = GlanceModifier
                        .background(Color(0xFF1E293B))
                        .cornerRadius(12.dp)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = cycleBadgeText,
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF60A5FA)),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // TODAY'S DATE
            Text(
                text = dateText,
                style = TextStyle(
                    color = ColorProvider(Color(0xFFF8FAFC)),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // DIVIDER
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF334155))
            ) {}

            Spacer(modifier = GlanceModifier.height(8.dp))

            // TODAY'S REAL TASKS LIST
            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks for today. Tap to add!",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF94A3B8)),
                        fontSize = 13.sp
                    )
                )
            } else {
                for (task in tasks.take(5)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = if (task.completed) "✓ " else "• ",
                            style = TextStyle(
                                color = ColorProvider(if (task.completed) Color(0xFF10B981) else Color(0xFF60A5FA)),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = task.title,
                            maxLines = 1,
                            style = TextStyle(
                                color = ColorProvider(if (task.completed) Color(0xFF64748B) else Color(0xFFCBD5E1)),
                                fontSize = 13.sp,
                                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                            )
                        )
                    }
                }
            }
        }
    }
}


class TodayTaskWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayTaskWidget()
}
