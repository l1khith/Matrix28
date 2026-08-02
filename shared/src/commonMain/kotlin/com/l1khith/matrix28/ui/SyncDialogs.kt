package com.l1khith.matrix28.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SyncDialog(
    onDismiss: () -> Unit,
    onSyncSystem: () -> Unit,
    onExportIcs: () -> Unit,
    onImportIcs: () -> Unit
) {
    val cardBg = Color(0xFF1E293B)
    val titleColor = Color.White
    val subtitleColor = Color(0xFF94A3B8)
    val accentBlue = Color(0xFF3B82F6)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.Black,
            border = BorderStroke(1.dp, Color(0xFF1D4ED8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Calendar Sync & Exchange",
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Card 1: System Sync
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSyncSystem() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1D4ED8).copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync",
                                tint = accentBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Sync Google Calendar / Outlook",
                                color = titleColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Imports events from Android's system calendars",
                                color = subtitleColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 2: Export ICS
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExportIcs() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Export ICS",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Export to iCalendar (.ics)",
                                color = titleColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Export with custom filename or copy ICS text",
                                color = subtitleColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 3: Import ICS
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onImportIcs() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(accentBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Import ICS",
                                tint = accentBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Import from iCalendar (.ics)",
                                color = titleColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Import via file selection or text paste",
                                color = subtitleColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close", color = accentBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun IcsExportDialog(
    onDismiss: () -> Unit,
    onCopyClipboard: () -> Unit,
    onSaveFile: (fileName: String) -> Unit
) {
    val options = listOf(
        "matrix28_schedule.ics",
        "fixed_calendar_2026.ics",
        "backup_agenda.ics"
    )
    var selectedOption by remember { mutableStateOf(options[0]) }
    var customFileName by remember { mutableStateOf("") }
    var isCustom by remember { mutableStateOf(false) }

    val primaryAccent = Color(0xFF10B981)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Black,
            border = BorderStroke(1.dp, Color(0xFF059669)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Export iCalendar (.ics)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select Filename Option:",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedOption = option
                                isCustom = false
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (!isCustom && selectedOption == option),
                            onClick = {
                                selectedOption = option
                                isCustom = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = primaryAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(option, color = Color.White, fontSize = 14.sp)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCustom = true }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isCustom,
                        onClick = { isCustom = true },
                        colors = RadioButtonDefaults.colors(selectedColor = primaryAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Custom filename...", color = Color.White, fontSize = 14.sp)
                }

                if (isCustom) {
                    OutlinedTextField(
                        value = customFileName,
                        onValueChange = { customFileName = it },
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        label = { Text("e.g. my_custom_events.ics", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryAccent,
                            unfocusedBorderColor = Color(0xFF1E293B)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val finalName = if (isCustom && customFileName.isNotBlank()) {
                                if (customFileName.endsWith(".ics")) customFileName else "$customFileName.ics"
                            } else {
                                selectedOption
                            }
                            onSaveFile(finalName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Export .ics File ($ {if (isCustom && customFileName.isNotBlank()) customFileName else selectedOption})", color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onCopyClipboard,
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color(0xFF64748B))
                    ) {
                        Text("Copy Raw ICS Code to Clipboard", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun IcsImportDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var icsText by remember { mutableStateOf("") }
    val primaryAccent = Color(0xFF3B82F6)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.Black,
            border = BorderStroke(1.dp, Color(0xFF1D4ED8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Import iCalendar (.ics)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Paste .ics Event Content:",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = icsText,
                    onValueChange = { icsText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    label = { Text("BEGIN:VCALENDAR ... END:VCALENDAR", color = Color(0xFF94A3B8)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = primaryAccent,
                        unfocusedBorderColor = Color(0xFF1E293B)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF94A3B8))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (icsText.isNotBlank()) {
                                onImport(icsText)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                        enabled = icsText.isNotBlank()
                    ) {
                        Text("Import Tasks", color = Color.White)
                    }
                }
            }
        }
    }
}
