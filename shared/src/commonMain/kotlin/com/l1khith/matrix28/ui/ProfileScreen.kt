package com.l1khith.matrix28.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.l1khith.matrix28.ui.theme.MatrixColors
import com.l1khith.matrix28.ui.theme.MatrixShapes
import com.l1khith.matrix28.utils.FixedCalendarHelper
import com.l1khith.matrix28.utils.currentTimeMillis

@Composable
fun ProfileScreen(
    onOpenSubscription: () -> Unit,
    onOpenCustomerCenter: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenMonthView: () -> Unit
) {
    val todayFixed = remember { FixedCalendarHelper.fromTimestamp(currentTimeMillis()) }
    val cycleDay = todayFixed.day
    val cycleMonth = todayFixed.month
    val progressPercent = remember(cycleDay) { ((cycleDay / 28f) * 100).toInt() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MatrixColors.Surface)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // User Header Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(96.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, MatrixColors.Primary, CircleShape)
                            .background(MatrixColors.SurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = MatrixColors.Primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MatrixColors.SurfaceContainer)
                            .border(1.dp, MatrixColors.OutlineVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = MatrixColors.Primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Power User",
                    color = MatrixColors.TextHeader,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Cycle",
                        tint = MatrixColors.Primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CYCLE $cycleMonth / DAY $cycleDay",
                        color = MatrixColors.TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Section 1: Performance Overview
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "PERFORMANCE OVERVIEW",
                    color = MatrixColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Cycle 14 Overview Card
                Card(
                    shape = MatrixShapes.Lg,
                    colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainerLow),
                    border = BorderStroke(1.dp, MatrixColors.OutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cycle $cycleMonth Overview",
                            color = MatrixColors.TextHeader,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier.size(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { cycleDay / 28f },
                                modifier = Modifier.fillMaxSize(),
                                color = MatrixColors.Primary,
                                strokeWidth = 8.dp,
                                trackColor = MatrixColors.OutlineVariant
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$progressPercent%",
                                    color = MatrixColors.Primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp
                                )
                                Text(
                                    text = "Day $cycleDay of 28",
                                    color = MatrixColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Efficiency Score Card
                Card(
                    shape = MatrixShapes.Lg,
                    colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainerLow),
                    border = BorderStroke(1.dp, MatrixColors.OutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Efficiency Score",
                                color = MatrixColors.TextSecondary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "88",
                                color = MatrixColors.TextHeader,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MatrixColors.SurfaceContainer)
                                .border(1.dp, MatrixColors.OutlineVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Efficiency",
                                tint = MatrixColors.Primary,
                                modifier = Modifier.size(28.dp)
                            )

                        }
                    }
                }
            }
        }

        // Section 2: Account Settings
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ACCOUNT SETTINGS",
                    color = MatrixColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Card(
                    shape = MatrixShapes.Lg,
                    colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainerLow),
                    border = BorderStroke(1.dp, MatrixColors.OutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ProfileSettingRow(
                            icon = Icons.Default.Star,
                            title = "Subscription",
                            onClick = onOpenSubscription
                        )

                        HorizontalDivider(color = MatrixColors.OutlineVariant, thickness = 1.dp)
                        ProfileSettingRow(
                            icon = Icons.Default.Lock,
                            title = "Security",
                            onClick = onOpenCustomerCenter
                        )
                        HorizontalDivider(color = MatrixColors.OutlineVariant, thickness = 1.dp)
                        ProfileSettingRow(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            onClick = onOpenSettings
                        )
                    }
                }
            }
        }

        // Section 3: Calendar Preferences
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "CALENDAR PREFERENCES",
                    color = MatrixColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Card(
                    shape = MatrixShapes.Lg,
                    colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainerLow),
                    border = BorderStroke(1.dp, MatrixColors.OutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ProfileValueRow(
                            icon = Icons.Default.DateRange,
                            title = "Cycle Start Date",
                            value = "Sunday",
                            onClick = onOpenSettings
                        )
                        HorizontalDivider(color = MatrixColors.OutlineVariant, thickness = 1.dp)
                        ProfileValueRow(
                            icon = Icons.Default.DateRange,
                            title = "Reminder Times",
                            value = "Default",
                            onClick = onOpenSettings
                        )
                    }
                }
            }
        }

        // Section 4: Support & Legal
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SUPPORT & LEGAL",
                    color = MatrixColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Card(
                    shape = MatrixShapes.Lg,
                    colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainerLow),
                    border = BorderStroke(1.dp, MatrixColors.OutlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ProfileSettingRow(
                            icon = Icons.Default.Info,
                            title = "Help Center",
                            onClick = {}
                        )
                        HorizontalDivider(color = MatrixColors.OutlineVariant, thickness = 1.dp)
                        ProfileSettingRow(
                            icon = Icons.Default.Info,
                            title = "Privacy Policy",
                            onClick = {}
                        )
                    }
                }
            }
        }

        // Section 5: Log Out Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                shape = MatrixShapes.Lg,
                colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainerLow),
                border = BorderStroke(1.dp, MatrixColors.Error.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Log Out",
                        tint = MatrixColors.Error
                    )


                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Log Out",
                        color = MatrixColors.Error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileSettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MatrixColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = MatrixColors.TextHeader,
                fontSize = 14.sp
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go",
            tint = MatrixColors.TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ProfileValueRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MatrixColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = MatrixColors.TextHeader,
                fontSize = 14.sp
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                color = MatrixColors.TextSecondary,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go",
                tint = MatrixColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
