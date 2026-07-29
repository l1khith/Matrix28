package com.l1khith.matrix28.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.l1khith.matrix28.data.AppTask
import com.l1khith.matrix28.utils.FixedCalendarHelper
import com.l1khith.matrix28.utils.FixedDate
import com.l1khith.matrix28.utils.PlatformTimePicker
import com.l1khith.matrix28.utils.currentTimeMillis
import com.l1khith.matrix28.viewmodel.FixedCalendarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixedCalendarApp(viewModel: FixedCalendarViewModel) {
    val selectedDate by viewModel.selectedDate
    val tasks by viewModel.tasksForSelectedDay
    val activeDates by viewModel.datesWithActiveTasks
    val recurringTasks by viewModel.recurringTasks

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<AppTask?>(null) }
    var showRecurringManager by remember { mutableStateOf(false) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var showIcsImportDialog by remember { mutableStateOf(false) }

    val todayFixed = remember { FixedCalendarHelper.fromTimestamp(currentTimeMillis()) }

    val backgroundColor = Color.Black
    val cardBackground = Color(0xFF1E293B)
    val primaryAccent = Color(0xFF2563EB) // Bright Blue
    val secondaryAccent = Color(0xFF3B82F6)
    val orangeAccent = Color(0xFFF97316)
    val textColorPrimary = Color.White
    val textColorSecondary = Color(0xFF94A3B8)
    val borderBlue = Color(0xFF1D4ED8)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "13-Month Planner",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColorPrimary
                    )
                },
                actions = {
                    IconButton(onClick = { showSyncDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync & Exchange",
                            tint = secondaryAccent
                        )
                    }
                    IconButton(onClick = { showRecurringManager = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Recurring Templates",
                            tint = secondaryAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    titleContentColor = textColorPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showAddTaskDialog = true
                },
                containerColor = primaryAccent,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
                .padding(horizontal = 16.dp)
        ) {
            MonthYearSelector(
                selectedDate = selectedDate,
                onDateChange = { viewModel.selectDate(it) },
                primaryColor = secondaryAccent,
                textColor = textColorPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            CalendarMatrix(
                selectedDate = selectedDate,
                activeDates = activeDates,
                todayFixed = todayFixed,
                onDateSelect = { viewModel.selectDate(it) },
                primaryColor = primaryAccent,
                orangeDotColor = orangeAccent,
                cardBg = backgroundColor,
                borderColor = borderBlue,
                textColor = textColorPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${FixedCalendarHelper.getMonthName(selectedDate.month)} ${selectedDate.day}, ${selectedDate.year}",
                    color = textColorPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = FixedCalendarHelper.getDayOfWeek(selectedDate),
                    color = orangeAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .border(BorderStroke(1.dp, borderBlue), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                if (tasks.isEmpty()) {
                    EmptyState(textColor = textColorSecondary)
                } else {
                    AgendaList(
                        tasks = tasks,
                        onToggleComplete = { viewModel.toggleTaskCompletion(it) },
                        onEdit = {
                            taskToEdit = it
                            showAddTaskDialog = true
                        },
                        onDelete = { viewModel.deleteTask(it) },
                        primaryColor = secondaryAccent,
                        orangeColor = orangeAccent,
                        textColor = textColorPrimary,
                        textColorSec = textColorSecondary,
                        cardBg = cardBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showAddTaskDialog) {
        AddEditTaskDialog(
            task = taskToEdit,
            onDismiss = { showAddTaskDialog = false },
            onSave = { id, title, desc, isReminder, time, priority ->
                viewModel.saveTask(id, title, desc, isReminder, time, priority)
                showAddTaskDialog = false
            },
            primaryColor = primaryAccent,
            backgroundColor = cardBackground,
            textColor = textColorPrimary,
            textColorSec = textColorSecondary
        )
    }

    if (showRecurringManager) {
        RecurringManagerDialog(
            recurringTasks = recurringTasks,
            onDismiss = { showRecurringManager = false },
            onSaveRecurringTask = { id, title, desc, recType, recDays, interval, priority, isActive, endDate, reminderTime ->
                viewModel.saveRecurringTask(id, title, desc, recType, recDays, interval, priority, isActive, endDate, reminderTime)
            },
            onDeleteRecurringTask = { viewModel.deleteRecurringTask(it) },
            onToggleActive = { viewModel.toggleRecurringTaskActive(it) },
            primaryColor = primaryAccent,
            backgroundColor = backgroundColor,
            textColor = textColorPrimary,
            textColorSec = textColorSecondary
        )
    }

    if (showSyncDialog) {
        SyncDialog(
            onDismiss = { showSyncDialog = false },
            onSyncSystem = {
                viewModel.importSystemCalendar()
                showSyncDialog = false
            },
            onExportIcs = {
                val ics = viewModel.getIcsExportString()
                com.l1khith.matrix28.utils.copyToClipboard(ics)
                showSyncDialog = false
            },
            onImportIcs = {
                showSyncDialog = false
                showIcsImportDialog = true
            }
        )
    }

    if (showIcsImportDialog) {
        IcsImportDialog(
            onDismiss = { showIcsImportDialog = false },
            onImport = { content ->
                viewModel.importFromIcsContent(content)
                showIcsImportDialog = false
            }
        )
    }
}

@Composable
fun MonthYearSelector(
    selectedDate: FixedDate,
    onDateChange: (FixedDate) -> Unit,
    primaryColor: Color,
    textColor: Color
) {
    var expandedMonth by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expandedMonth = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = FixedCalendarHelper.getMonthName(selectedDate.month),
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Month",
                    tint = primaryColor
                )
            }

            DropdownMenu(
                expanded = expandedMonth,
                onDismissRequest = { expandedMonth = false },
                modifier = Modifier.background(Color(0xFF1E293B))
            ) {
                for (m in 1..13) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${FixedCalendarHelper.getMonthName(m)} (M$m)",
                                color = Color.White
                            )
                        },
                        onClick = {
                            val newDay = if (selectedDate.day == 29 && m != 6 && m != 13) 28 else selectedDate.day
                            val isLD = m == 6 && newDay == 29
                            val isYD = m == 13 && newDay == 29
                            onDateChange(FixedDate(selectedDate.year, m, newDay, isLeapDay = isLD, isYearDay = isYD))
                            expandedMonth = false
                        }
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                onDateChange(selectedDate.copy(year = selectedDate.year - 1))
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Prev Year",
                    tint = primaryColor
                )
            }
            Text(
                text = selectedDate.year.toString(),
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(onClick = {
                onDateChange(selectedDate.copy(year = selectedDate.year + 1))
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Year",
                    tint = primaryColor
                )
            }
        }
    }
}

@Composable
fun CalendarMatrix(
    selectedDate: FixedDate,
    activeDates: Set<String>,
    todayFixed: FixedDate,
    onDateSelect: (FixedDate) -> Unit,
    primaryColor: Color,
    orangeDotColor: Color,
    cardBg: Color,
    borderColor: Color,
    textColor: Color
) {
    val weekDays = arrayOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    val isLeap = FixedCalendarHelper.isLeapYear(selectedDate.year)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (day in weekDays) {
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        for (row in 0 until 4) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 1..7) {
                    val dayNum = row * 7 + col
                    val cellDate = FixedDate(selectedDate.year, selectedDate.month, dayNum)
                    val cellDateStr = cellDate.toString()
                    val hasTasks = activeDates.contains(cellDateStr)
                    val isCellSelected = selectedDate.month == cellDate.month && selectedDate.day == dayNum
                    val isCellToday = todayFixed.year == cellDate.year && todayFixed.month == cellDate.month && todayFixed.day == dayNum

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCellSelected -> primaryColor
                                    isCellToday -> primaryColor.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                width = if (isCellToday && !isCellSelected) 1.dp else 0.dp,
                                color = if (isCellToday && !isCellSelected) primaryColor else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                onDateSelect(cellDate)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = dayNum.toString(),
                                color = if (isCellSelected) Color.White else textColor,
                                fontWeight = if (isCellSelected || isCellToday) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            if (hasTasks) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(if (isCellSelected) Color.White else orangeDotColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedDate.month == 6 && isLeap) {
            Spacer(modifier = Modifier.height(10.dp))
            val leapDay = FixedDate(selectedDate.year, 6, 29, isLeapDay = true)
            val isLeapSelected = selectedDate.month == 6 && selectedDate.day == 29
            val isLeapToday = todayFixed.year == selectedDate.year && todayFixed.month == 6 && todayFixed.day == 29
            val hasTasks = activeDates.contains(leapDay.toString())

            SpecialDayCard(
                label = "Leap Day (June 29)",
                isSelected = isLeapSelected,
                isToday = isLeapToday,
                hasTasks = hasTasks,
                onClick = { onDateSelect(leapDay) },
                primaryColor = primaryColor,
                orangeDotColor = orangeDotColor,
                textColor = textColor
            )
        }

        if (selectedDate.month == 13) {
            Spacer(modifier = Modifier.height(10.dp))
            val yearDay = FixedDate(selectedDate.year, 13, 29, isYearDay = true)
            val isYearSelected = selectedDate.month == 13 && selectedDate.day == 29
            val isYearToday = todayFixed.year == selectedDate.year && todayFixed.month == 13 && todayFixed.day == 29
            val hasTasks = activeDates.contains(yearDay.toString())

            SpecialDayCard(
                label = "Year Day (December 29)",
                isSelected = isYearSelected,
                isToday = isYearToday,
                hasTasks = hasTasks,
                onClick = { onDateSelect(yearDay) },
                primaryColor = primaryColor,
                orangeDotColor = orangeDotColor,
                textColor = textColor
            )
        }
    }
}

@Composable
fun SpecialDayCard(
    label: String,
    isSelected: Boolean,
    isToday: Boolean,
    hasTasks: Boolean,
    onClick: () -> Unit,
    primaryColor: Color,
    orangeDotColor: Color,
    textColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) primaryColor else Color.Black
        ),
        border = BorderStroke(
            width = if (isToday && !isSelected) 1.dp else 0.dp,
            color = if (isToday && !isSelected) primaryColor else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐ $label",
                color = if (isSelected) Color.White else textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            if (hasTasks) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else orangeDotColor)
                )
            }
        }
    }
}

@Composable
fun AgendaList(
    tasks: List<AppTask>,
    onToggleComplete: (AppTask) -> Unit,
    onEdit: (AppTask) -> Unit,
    onDelete: (AppTask) -> Unit,
    primaryColor: Color,
    orangeColor: Color,
    textColor: Color,
    textColorSec: Color,
    cardBg: Color
) {
    val reminders = remember(tasks) { tasks.filter { it.reminder } }
    val todos = remember(tasks) { tasks.filter { !it.reminder && it.isGenerated == 0 } }
    val recurring = remember(tasks) { tasks.filter { !it.reminder && it.isGenerated == 1 } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (reminders.isNotEmpty()) {
            item {
                Text(
                    text = "⏰ REMINDERS (TIME-CRITICAL)",
                    color = orangeColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp, top = 2.dp)
                )
            }
            items(reminders, key = { it.id }) { task ->
                ReminderItem(
                    task = task,
                    onToggleComplete = onToggleComplete,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    textColor = textColor,
                    textColorSec = textColorSec,
                    accentColor = orangeColor,
                    cardBg = cardBg
                )
            }
        }

        if (todos.isNotEmpty()) {
            item {
                Text(
                    text = "✅ DAILY TO-DOS (STATE-CRITICAL)",
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
                )
            }
            items(todos, key = { it.id }) { task ->
                TodoItem(
                    task = task,
                    onToggleComplete = onToggleComplete,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    textColor = textColor,
                    textColorSec = textColorSec,
                    cardBg = cardBg
                )
            }
        }

        if (recurring.isNotEmpty()) {
            item {
                Text(
                    text = "🔄 RECURRING PLANS",
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
                )
            }
            items(recurring, key = { it.id }) { task ->
                TodoItem(
                    task = task,
                    onToggleComplete = onToggleComplete,
                    onEdit = {},
                    onDelete = onDelete,
                    textColor = textColor,
                    textColorSec = textColorSec,
                    cardBg = cardBg
                )
            }
        }
    }
}

@Composable
fun ReminderItem(
    task: AppTask,
    onToggleComplete: (AppTask) -> Unit,
    onEdit: (AppTask) -> Unit,
    onDelete: (AppTask) -> Unit,
    textColor: Color,
    textColorSec: Color,
    accentColor: Color,
    cardBg: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleComplete(task) },
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, if (task.completed) Color.Green else accentColor, CircleShape)
                        .background(if (task.completed) Color.Green.copy(alpha = 0.2f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.completed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.Green,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = task.reminderTime ?: "12:00",
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = task.title,
                            color = if (task.completed) textColorSec else textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textDecoration = if (task.completed) TextDecoration.LineThrough else null
                        )
                    }
                    if (!task.description.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = task.description,
                            color = textColorSec,
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                }
            }

            Row {
                IconButton(
                    onClick = { onEdit(task) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Reminder",
                        tint = textColorSec,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = { onDelete(task) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Reminder",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItem(
    task: AppTask,
    onToggleComplete: (AppTask) -> Unit,
    onEdit: (AppTask) -> Unit,
    onDelete: (AppTask) -> Unit,
    textColor: Color,
    textColorSec: Color,
    cardBg: Color
) {
    val isGeneratedTask = task.isGenerated == 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleComplete(task) },
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Square checkbox visual like in screenshot
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.5.dp, if (task.completed) Color.Green else Color(0xFF64748B), RoundedCornerShape(4.dp))
                        .background(if (task.completed) Color.Green.copy(alpha = 0.2f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.completed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.Green,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = when (task.priority) {
                                    3 -> "H"
                                    2 -> "M"
                                    else -> "L"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = task.title,
                            color = if (task.completed) textColorSec else textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textDecoration = if (task.completed) TextDecoration.LineThrough else null
                        )
                    }
                    if (!task.description.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = task.description,
                            color = textColorSec,
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                }
            }

            Row {
                if (!isGeneratedTask) {
                    IconButton(
                        onClick = { onEdit(task) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = textColorSec,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                IconButton(
                    onClick = { onDelete(task) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(textColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "No tasks",
                tint = textColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No plans for this date.",
                color = textColor,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Text(
                text = "Tap + to add a task or reminder",
                color = textColor.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AddEditTaskDialog(
    task: AppTask?,
    onDismiss: () -> Unit,
    onSave: (
        id: String?,
        title: String,
        description: String?,
        isReminder: Boolean,
        reminderTime: String?,
        priority: Int
    ) -> Unit,
    primaryColor: Color,
    backgroundColor: Color,
    textColor: Color,
    textColorSec: Color
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var isReminder by remember { mutableStateOf(task?.reminder ?: false) }
    var reminderTime by remember { mutableStateOf(task?.reminderTime ?: "12:00") }
    var priority by remember { mutableStateOf(task?.priority ?: 1) }
    var showTimePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Black,
            border = BorderStroke(1.dp, Color(0xFF1D4ED8))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (task == null) "New Plan" else "Modify Plan",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (Required)", color = textColorSec) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFF1E293B)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes (Optional)", color = textColorSec) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFF1E293B)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminder Alert",
                            tint = if (isReminder) primaryColor else textColorSec,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Time-Critical Reminder", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Set specific alarm time", color = textColorSec, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = isReminder,
                        onCheckedChange = { isReminder = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = primaryColor)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isReminder) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .clickable { showTimePicker = true }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Trigger Alarm Time", color = textColor, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(reminderTime, color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Pick Time", tint = primaryColor)
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Task Priority", color = textColorSec, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val priorities = listOf(
                                Triple(1, "Low", Color(0xFF94A3B8)),
                                Triple(2, "Medium", Color(0xFFF59E0B)),
                                Triple(3, "High", Color(0xFFEF4444))
                            )
                            for ((pVal, label, color) in priorities) {
                                val isSelected = priority == pVal
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                        .clickable { priority = pVal },
                                    border = BorderStroke(
                                        width = if (isSelected) 1.5.dp else 0.dp,
                                        color = if (isSelected) color else Color.Transparent
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) color.copy(alpha = 0.25f) else Color(0xFF1E293B)
                                    )
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) color else textColorSec,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = textColorSec)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.trim().isNotEmpty()) {
                                onSave(task?.id, title.trim(), description.trim(), isReminder, reminderTime, priority)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        enabled = title.trim().isNotEmpty()
                    ) {
                        Text("Save Plan", color = Color.White)
                    }
                }
            }
        }
    }

    PlatformTimePicker(
        show = showTimePicker,
        initialTime = reminderTime,
        onDismiss = { showTimePicker = false },
        onTimeSelected = { time: String ->
            reminderTime = time
            showTimePicker = false
        }
    )
}
