package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.CloudUser
import com.example.ui.theme.BlackCard
import com.example.ui.theme.BlackCardBorder
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedLight
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.VerifiedGreen
import com.example.ui.theme.VerifiedGreenContainer

@Composable
fun OwnerCloudConsoleScreen(
    currentUser: CloudUser?,
    users: List<CloudUser>,
    onUpdateStorageQuota: (email: String, newQuotaGb: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var editingUser by remember { mutableStateOf<CloudUser?>(null) }

    val nonOwnerUsers = users.filter { !it.isOwner }
    val totalDistributedGb = users.sumOf { it.storageTotalGb }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(8.dp))

            // Master Owner Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CrimsonContainer, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = BlackCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = CrimsonRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Cloud Owner Console",
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Master Controller: ${currentUser?.email ?: "redwanliton9@gmail.com"}",
                                    color = CrimsonRedLight,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(VerifiedGreenContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "OWNER: 500 GB",
                                color = VerifiedGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "As the sole owner of www.REDTEXT.com.BD, you have full authority to update each user's cloud storage quota day to day.",
                        color = TextGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuotaStatBox(
                            label = "Total People",
                            value = "${users.size}",
                            icon = Icons.Default.Group
                        )
                        QuotaStatBox(
                            label = "Default Initial",
                            value = "5 GB",
                            icon = Icons.Default.Storage
                        )
                        QuotaStatBox(
                            label = "Total Capacity",
                            value = "${totalDistributedGb.toInt()} GB",
                            icon = Icons.Default.TrendingUp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PEOPLE REGISTERED ON CLOUD (${nonOwnerUsers.size})",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Users list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(nonOwnerUsers, key = { it.email }) { user ->
                    UserQuotaCard(
                        user = user,
                        onEditQuota = { editingUser = user },
                        onQuickAdd5Gb = {
                            val newQuota = user.storageTotalGb + 5.0
                            onUpdateStorageQuota(user.email, newQuota)
                            Toast.makeText(context, "Added +5GB to ${user.name}! (Now ${newQuota.toInt()} GB)", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Edit Quota Dialog
        editingUser?.let { user ->
            UpdateQuotaDialog(
                user = user,
                onDismiss = { editingUser = null },
                onSaveQuota = { newQuota ->
                    onUpdateStorageQuota(user.email, newQuota)
                    editingUser = null
                    Toast.makeText(context, "Updated ${user.name}'s quota to $newQuota GB", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun QuotaStatBox(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PitchBlack)
            .border(1.dp, BlackCardBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun UserQuotaCard(
    user: CloudUser,
    onEditQuota: () -> Unit,
    onQuickAdd5Gb: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BlackCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = BlackCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = user.name,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.email,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Storage badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CrimsonContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${user.storageTotalGb.toInt()} GB ALLOCATED",
                        color = CrimsonRedLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Used storage detail
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Used: ${user.storageUsedGb} GB (${String.format(java.util.Locale.US, "%.1f", (user.storageUsedGb / user.storageTotalGb) * 100)}%)",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onQuickAdd5Gb,
                        colors = ButtonDefaults.textButtonColors(contentColor = VerifiedGreen)
                    ) {
                        Text("+5 GB Bonus", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Button(
                        onClick = onEditQuota,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update Quota", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateQuotaDialog(
    user: CloudUser,
    onDismiss: () -> Unit,
    onSaveQuota: (Double) -> Unit
) {
    var quotaInput by remember { mutableStateOf(user.storageTotalGb.toInt().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(16.dp))
                .background(BlackSurface)
                .border(1.dp, CrimsonContainer, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Update Cloud Storage Quota",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "User: ${user.name} (${user.email})",
                    color = CrimsonRedLight,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select Quick Quota Option:",
                    color = TextGray,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("5", "15", "50", "100").forEach { q ->
                        val isSelected = quotaInput == q
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CrimsonRed else BlackCard)
                                .border(1.dp, if (isSelected) CrimsonRedLight else BlackCardBorder, RoundedCornerShape(8.dp))
                                .clickable { quotaInput = q }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$q GB",
                                color = if (isSelected) TextWhite else TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Or Enter Custom GB Quota:", color = TextGray, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = quotaInput,
                    onValueChange = { quotaInput = it.filter { char -> char.isDigit() } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonRed,
                        unfocusedBorderColor = BlackCardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = CrimsonRed,
                        focusedContainerColor = PitchBlack,
                        unfocusedContainerColor = PitchBlack
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = TextGray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val newQuota = quotaInput.toDoubleOrNull() ?: 5.0
                            onSaveQuota(newQuota)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Apply Quota", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
