package com.l1khith.matrix28.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.l1khith.matrix28.repository.CategoryRepository
import com.l1khith.matrix28.ui.theme.MatrixColors
import com.l1khith.matrix28.ui.theme.MatrixShapes
import com.l1khith.matrix28.utils.showPlatformToast

@Composable
fun CategorySelectionUI(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    isProActive: Boolean,
    onOpenPaywall: () -> Unit
) {
    val customCategories by CategoryRepository.customCategories.collectAsState()
    val allCategories = remember(customCategories) { CategoryRepository.getAllCategories() }

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryInput by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Category",
                color = MatrixColors.TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            if (!isProActive) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Pro Feature",
                        tint = MatrixColors.Primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PRO",
                        color = MatrixColors.Primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(allCategories) { category ->
                val isSelected = category.equals(selectedCategory, ignoreCase = true)
                val chipColor = getCategoryColor(category)

                Card(
                    modifier = Modifier.clickable { onCategorySelected(category) },
                    shape = MatrixShapes.Xl,
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) chipColor.copy(alpha = 0.2f) else Color.Transparent
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) chipColor else MatrixColors.OutlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(chipColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category,
                            color = if (isSelected) chipColor else MatrixColors.TextHeader,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // + Add Custom Category Button
            item {
                Card(
                    modifier = Modifier.clickable {
                        if (isProActive) {
                            newCategoryInput = ""
                            validationError = null
                            showAddCategoryDialog = true
                        } else {
                            showPlatformToast("Custom Categories are a Pro Feature. Switch to Pro mode to create custom categories!")
                            onOpenPaywall()
                        }
                    },
                    shape = MatrixShapes.Xl,
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, MatrixColors.OutlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isProActive) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Pro Locked",
                                tint = MatrixColors.Primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = "+ Custom",
                            color = if (isProActive) MatrixColors.Primary else MatrixColors.TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("New Custom Category", color = MatrixColors.TextHeader, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCategoryInput,
                        onValueChange = {
                            newCategoryInput = it
                            validationError = null
                        },
                        placeholder = { Text("Category Name (e.g. Finance)", color = MatrixColors.TextSecondary.copy(alpha = 0.5f)) },
                        singleLine = true,
                        isError = validationError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MatrixColors.TextHeader,
                            unfocusedTextColor = MatrixColors.TextHeader,
                            focusedBorderColor = MatrixColors.Primary,
                            unfocusedBorderColor = MatrixColors.OutlineVariant
                        )
                    )
                    if (validationError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = validationError!!,
                            color = MatrixColors.Error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val result = CategoryRepository.addCustomCategory(newCategoryInput)
                        if (result.first) {
                            onCategorySelected(newCategoryInput.trim())
                            showAddCategoryDialog = false
                            showPlatformToast("Custom Category '${newCategoryInput.trim()}' created!")
                        } else {
                            validationError = result.second
                        }
                    }
                ) {
                    Text("Add", color = MatrixColors.Primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel", color = MatrixColors.TextSecondary)
                }
            },
            containerColor = MatrixColors.SurfaceContainerLow
        )
    }
}

private fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "work" -> MatrixColors.Secondary
        "personal" -> MatrixColors.Tertiary
        "health" -> MatrixColors.Error
        "finance" -> Color(0xFF10B981) // Emerald Green
        "social" -> Color(0xFF8B5CF6) // Purple
        "education" -> Color(0xFFF59E0B) // Amber
        else -> MatrixColors.Primary
    }
}
