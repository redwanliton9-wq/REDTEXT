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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.CloudFile
import com.example.data.entity.CloudUser
import com.example.ui.components.StorageQuotaProgressBar
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CloudDataScreen(
    currentUser: CloudUser?,
    files: List<CloudFile>,
    allUsers: List<CloudUser>,
    onUploadFile: (fileName: String, fileType: String, sizeMb: Double, snippet: String) -> Unit,
    onDeleteFile: (CloudFile) -> Unit,
    onSwitchUser: (String) -> Unit,
    onUpgradeStorageQuota: (Double) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showUploadDialog by remember { mutableStateOf(false) }
    var showSwitchDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    val totalStorageGb = currentUser?.storageTotalGb ?: 5.0
    val userFiles = files.filter { it.userEmail == (currentUser?.email ?: "") }
    val totalUsedMb = userFiles.sumOf { it.sizeMb }
    val usedGb = totalUsedMb / 1024.0
    val progress = (usedGb / totalStorageGb).toFloat().coerceIn(0f, 1f)

    val categories = listOf("All", "DOC", "IMAGE", "VIDEO", "ARCHIVE", "DATA")

    val filteredFiles = userFiles.filter { file ->
        val matchesCategory = selectedCategory == "All" || file.fileType.equals(selectedCategory, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() ||
            file.fileName.contains(searchQuery, ignoreCase = true) ||
            file.contentSnippet.contains(searchQuery, ignoreCase = true) ||
            file.fileType.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(8.dp))

            // User Identity & Switch Account Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BlackCard)
                    .border(1.dp, BlackCardBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (currentUser?.isOwner == true) CrimsonContainer else PitchBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (currentUser?.isOwner == true) Icons.Default.CloudDone else Icons.Default.Cloud,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.name ?: "Cloud User",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentUser?.isOwner == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "★ OWNER",
                                    color = CrimsonRedLight,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = currentUser?.email ?: "www.REDTEXT.com.BD",
                            color = TextGray,
                            fontSize = 10.sp
                        )
                    }
                }

                TextButton(
                    onClick = { showSwitchDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = CrimsonRedLight)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Switch Account", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Storage Quota Progress Bar Component (5GB initial to 500GB upgradeable, showing used vs remaining)
            StorageQuotaProgressBar(
                usedGb = usedGb,
                totalStorageGb = totalStorageGb,
                isOwner = currentUser?.isOwner == true,
                onUpgradeTo500Gb = {
                    onUpgradeStorageQuota(500.0)
                    Toast.makeText(context, "Storage upgraded from 5 GB to 500 GB Master Tier!", Toast.LENGTH_SHORT).show()
                },
                onResetTo5Gb = {
                    onUpgradeStorageQuota(5.0)
                    Toast.makeText(context, "Storage reset to 5 GB Initial Quota", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main Dashboard Search Bar (Search encrypted files by name or content snippet)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search encrypted files by name or snippet...", color = TextMuted, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CrimsonRed,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_search_bar"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonRed,
                    unfocusedBorderColor = BlackCardBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = CrimsonRed,
                    focusedContainerColor = BlackCard,
                    unfocusedContainerColor = BlackCard
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (searchQuery.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Found ${filteredFiles.size} encrypted file${if (filteredFiles.size == 1) "" else "s"} matching \"$searchQuery\"",
                        color = CrimsonRedLight,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Clear",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clickable { searchQuery = "" }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Category chips and Upload button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CrimsonRed else BlackCard)
                                .border(
                                    1.dp,
                                    if (isSelected) CrimsonRedLight else BlackCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) TextWhite else TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showUploadDialog = true },
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("upload_cloud_file_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonRed,
                        contentColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cloud Files List
            if (filteredFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No files uploaded in this folder",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Tap 'Upload' to store files securely in Redtext Cloud",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredFiles, key = { it.id }) { file ->
                        CloudFileCard(
                            file = file,
                            searchQuery = searchQuery,
                            dateFormatter = dateFormatter,
                            onCopyShareLink = {
                                clipboardManager.setText(AnnotatedString(file.shareLink))
                                Toast.makeText(context, "Link copied: ${file.shareLink}", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = { onDeleteFile(file) }
                        )
                    }
                }
            }
        }

        // Upload Dialog
        if (showUploadDialog) {
            UploadFileDialog(
                onDismiss = { showUploadDialog = false },
                onUpload = { name, type, sizeMb, snippet ->
                    onUploadFile(name, type, sizeMb, snippet)
                    showUploadDialog = false
                    Toast.makeText(context, "Uploaded to www.REDTEXT.com.BD cloud", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Switch Account Dialog (Test any person's login)
        if (showSwitchDialog) {
            SwitchAccountDialog(
                allUsers = allUsers,
                currentEmail = currentUser?.email ?: "",
                onDismiss = { showSwitchDialog = false },
                onSelectUser = { email ->
                    onSwitchUser(email)
                    showSwitchDialog = false
                    Toast.makeText(context, "Switched cloud account to: $email", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
private fun CloudFileCard(
    file: CloudFile,
    searchQuery: String,
    dateFormatter: SimpleDateFormat,
    onCopyShareLink: () -> Unit,
    onDelete: () -> Unit
) {
    val fileIcon = when (file.fileType) {
        "DOC" -> Icons.Default.Description
        "IMAGE" -> Icons.Default.Image
        "VIDEO" -> Icons.Default.Movie
        "ARCHIVE" -> Icons.Default.Archive
        else -> Icons.Default.Storage
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BlackCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = BlackCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CrimsonContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = fileIcon,
                            contentDescription = null,
                            tint = CrimsonRedLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = file.fileName,
                            color = if (searchQuery.isNotBlank() && file.fileName.contains(searchQuery, ignoreCase = true)) CrimsonRedLight else TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${file.sizeMb} MB • ${dateFormatter.format(Date(file.uploadedAt))} • Encrypted",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopyShareLink, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = CrimsonRedLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Encrypted Content Snippet preview when available
            if (file.contentSnippet.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(PitchBlack)
                        .border(
                            1.dp,
                            if (searchQuery.isNotBlank() && file.contentSnippet.contains(searchQuery, ignoreCase = true)) CrimsonContainer else BlackCardBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CrimsonRedLight,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = file.contentSnippet,
                        color = if (searchQuery.isNotBlank() && file.contentSnippet.contains(searchQuery, ignoreCase = true)) TextWhite else TextGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadFileDialog(
    onDismiss: () -> Unit,
    onUpload: (name: String, type: String, sizeMb: Double, snippet: String) -> Unit
) {
    var fileName by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("DOC") }
    var sizeMb by remember { mutableDoubleStateOf(24.5) }
    var snippet by remember { mutableStateOf("") }

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
                    text = "Upload Data to Redtext Cloud",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Stored securely on www.REDTEXT.com.BD",
                    color = CrimsonRedLight,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "File Name", color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    placeholder = { Text("e.g. My_Project_Backup.zip", color = TextMuted, fontSize = 13.sp) },
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

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "File Type", color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("DOC", "IMAGE", "VIDEO", "ARCHIVE", "DATA").forEach { type ->
                        val isSelected = fileType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CrimsonRed else BlackCard)
                                .clickable { fileType = type }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) TextWhite else TextGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Estimated File Size", color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5.0 to "5 MB", 50.0 to "50 MB", 250.0 to "250 MB", 1024.0 to "1 GB").forEach { (sz, label) ->
                        val isSelected = sizeMb == sz
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CrimsonRed else BlackCard)
                                .clickable { sizeMb = sz }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) TextWhite else TextGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Encrypted Content Snippet / Key Notes (Optional)", color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = snippet,
                    onValueChange = { snippet = it },
                    placeholder = { Text("e.g. Master seed phrase, server backup credentials...", color = TextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_file_snippet_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CrimsonRed,
                        unfocusedBorderColor = BlackCardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = CrimsonRed,
                        focusedContainerColor = PitchBlack,
                        unfocusedContainerColor = PitchBlack
                    ),
                    maxLines = 2,
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
                            val finalName = if (fileName.isNotBlank()) fileName else "Redtext_File_${System.currentTimeMillis() % 1000}.${fileType.lowercase()}"
                            onUpload(finalName, fileType, sizeMb, snippet)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Upload Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SwitchAccountDialog(
    allUsers: List<CloudUser>,
    currentEmail: String,
    onDismiss: () -> Unit,
    onSelectUser: (String) -> Unit
) {
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
                    text = "Switch Cloud Account",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Test access from different people registered in the cloud",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allUsers) { user ->
                        val isCurrent = user.email == currentEmail
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) CrimsonContainer else BlackCard)
                                .border(
                                    1.dp,
                                    if (isCurrent) CrimsonRed else BlackCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectUser(user.email) }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.name,
                                            color = TextWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (user.isOwner) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "★ OWNER 500GB",
                                                color = CrimsonRedLight,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${user.email} • Quota: ${user.storageTotalGb.toInt()} GB",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                if (isCurrent) {
                                    Text(
                                        text = "ACTIVE",
                                        color = VerifiedGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close", color = TextGray)
                }
            }
        }
    }
}
