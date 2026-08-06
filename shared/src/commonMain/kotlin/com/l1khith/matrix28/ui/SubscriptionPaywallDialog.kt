package com.l1khith.matrix28.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.l1khith.matrix28.billing.SubscriptionManager
import com.l1khith.matrix28.ui.theme.MatrixColors
import com.l1khith.matrix28.ui.theme.MatrixShapes
import com.revenuecat.purchases.kmp.models.Package

@Composable
fun SubscriptionPaywallDialog(
    onDismiss: () -> Unit,
    onPurchaseSuccess: () -> Unit = {}
) {
    val offerings by SubscriptionManager.offerings.collectAsState()
    val isProActive by SubscriptionManager.isProActive.collectAsState()
    val isLoading by SubscriptionManager.isLoading.collectAsState()
    val errorMessage by SubscriptionManager.errorMessage.collectAsState()

    val currentOffering = remember(offerings) { offerings?.current }
    val availablePackages = remember(currentOffering) { currentOffering?.availablePackages ?: emptyList() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MatrixShapes.Lg,
            colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainerLow),
            border = BorderStroke(1.dp, MatrixColors.OutlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Pro",
                            tint = MatrixColors.Secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Matrix 28 Pro",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MatrixColors.TextHeader
                            )
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MatrixColors.TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isProActive) {
                    Surface(
                        shape = MatrixShapes.Md,
                        color = MatrixColors.TertiaryContainer.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, MatrixColors.Tertiary),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "🎉 You are currently subscribed to Matrix 28 Pro!",
                            color = MatrixColors.Tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Text(
                        text = "Unlock Premium Features & Ad-Free Experience",
                        color = MatrixColors.TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MatrixColors.Error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (isLoading) {
                        CircularProgressIndicator(color = MatrixColors.Primary)
                    } else if (availablePackages.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainer),
                            shape = MatrixShapes.Md,
                            border = BorderStroke(1.dp, MatrixColors.OutlineVariant),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "RevenueCat SDK Configured",
                                    color = MatrixColors.TextHeader,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Test Store API Key is active.\nWhen products are added in RevenueCat Dashboard (app.revenuecat.com), they will automatically render here.",
                                    color = MatrixColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(availablePackages, key = { it.identifier }) { pkg ->
                                PackageItem(
                                    packageToPurchase = pkg,
                                    onSelect = {
                                        SubscriptionManager.purchasePackage(
                                            packageToPurchase = pkg,
                                            onSuccess = {
                                                onPurchaseSuccess()
                                                onDismiss()
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            SubscriptionManager.restorePurchases(
                                onSuccess = { onPurchaseSuccess() }
                            )
                        }
                    ) {
                        Text("Restore Purchases", color = MatrixColors.Primary, fontSize = 12.sp)
                    }

                    TextButton(onClick = onDismiss) {
                        Text("Dismiss", color = MatrixColors.TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PackageItem(
    packageToPurchase: Package,
    onSelect: () -> Unit
) {
    val storeProduct = packageToPurchase.storeProduct
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = MatrixShapes.Md,
        colors = CardDefaults.cardColors(containerColor = MatrixColors.SurfaceContainer),
        border = BorderStroke(1.dp, MatrixColors.Primary)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = storeProduct.title,
                    color = MatrixColors.TextHeader,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = storeProduct.localizedDescription ?: "",
                    color = MatrixColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
            Text(
                text = storeProduct.price.formatted,
                color = MatrixColors.Tertiary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

