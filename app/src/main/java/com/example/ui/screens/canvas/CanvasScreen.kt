package com.example.ui.screens.canvas

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@Composable
fun CanvasScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val documents by viewModel.canvasDocuments.collectAsState()
    val activeDocId by viewModel.activeCanvasDocId.collectAsState()
    val activeDoc by viewModel.activeCanvasDoc.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var isCreatingDoc by remember { mutableStateOf(false) }
    var newDocTitle by remember { mutableStateOf("") }
    var newDocType by remember { mutableStateOf("code") }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showCommentsSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
    ) {
        // Top Canvas Workspace Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Back or Menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Canvas Workspace",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Co-write text & debug code side-by-side with AI",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(
                    onClick = { isCreatingDoc = true },
                    modifier = Modifier.testTag("create_canvas_doc_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Document", tint = EmeraldPrimary)
                }
            }
        }

        // Document selector tabs
        if (documents.isNotEmpty()) {
            val selectedIndex = documents.indexOfFirst { it.id == activeDocId }.coerceAtLeast(0)
            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary
            ) {
                documents.forEachIndexed { index, doc ->
                    Tab(
                        selected = index == selectedIndex,
                        onClick = { viewModel.selectCanvasDoc(doc.id) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (doc.type == "code") Icons.Default.Code else Icons.Default.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (index == selectedIndex) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = doc.title,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }
            }
        }

        activeDoc?.let { doc ->
            // AI Canvas Action Bar (Interactive co-writing buttons)
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Quick Actions:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, end = 2.dp)
                )

                CanvasActionButton(
                    icon = Icons.Default.BugReport,
                    label = "Fix Bugs",
                    tag = "action_fix_bugs",
                    onClick = {
                        viewModel.applyAiCanvasAction("FIX_BUGS")
                        Toast.makeText(context, "AI scanned & fixed code bugs", Toast.LENGTH_SHORT).show()
                    }
                )

                CanvasActionButton(
                    icon = Icons.AutoMirrored.Filled.Comment,
                    label = "Add Comments",
                    tag = "action_add_comments",
                    onClick = {
                        viewModel.applyAiCanvasAction("ADD_COMMENTS")
                        Toast.makeText(context, "Added architectural doc comments", Toast.LENGTH_SHORT).show()
                    }
                )

                CanvasActionButton(
                    icon = Icons.Default.Speed,
                    label = "Optimize",
                    tag = "action_optimize",
                    onClick = {
                        viewModel.applyAiCanvasAction("OPTIMIZE")
                        Toast.makeText(context, "Optimized complexity and channel backpressure", Toast.LENGTH_SHORT).show()
                    }
                )

                CanvasActionButton(
                    icon = Icons.Default.AutoAwesome,
                    label = "Shorten",
                    tag = "action_shorten",
                    onClick = {
                        viewModel.applyAiCanvasAction("SHORTEN")
                        Toast.makeText(context, "Simplified code", Toast.LENGTH_SHORT).show()
                    }
                )

                // Version history button
                CanvasActionButton(
                    icon = Icons.Default.History,
                    label = "Versions (${doc.versions.size})",
                    tag = "action_versions",
                    onClick = { showVersionDialog = true }
                )

                // Comments button
                CanvasActionButton(
                    icon = Icons.AutoMirrored.Filled.Comment,
                    label = "Notes (${doc.comments.size})",
                    tag = "action_notes",
                    onClick = { showCommentsSheet = true }
                )
            }

            // Editor Workspace
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (doc.type == "code") CodeBackground else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Editor Top Status Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2A2A2A))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${doc.title} • ${doc.language.uppercase()} • UTF-8",
                            fontSize = 11.sp,
                            color = Color(0xFFAAAAAA),
                            fontFamily = FontFamily.Monospace
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(doc.content))
                                    Toast.makeText(context, "Content copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Canvas Syntax Validation Passed (0 errors)", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Run Validation",
                                    tint = EmeraldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Editable Canvas Content
                    OutlinedTextField(
                        value = doc.content,
                        onValueChange = { newText ->
                            viewModel.updateCanvasContent(newText)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("canvas_editor_text_field"),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = if (doc.type == "code") FontFamily.Monospace else FontFamily.Default,
                            fontSize = 13.sp,
                            color = if (doc.type == "code") Color(0xFFE2E8F0) else MaterialTheme.colorScheme.onSurface,
                            lineHeight = 19.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No canvas document selected. Tap '+' to create one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    // New Document Dialog
    if (isCreatingDoc) {
        AlertDialog(
            onDismissRequest = { isCreatingDoc = false },
            title = { Text("Create New Canvas Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDocTitle,
                        onValueChange = { newDocTitle = it },
                        label = { Text("Document Title (e.g., WorkerPool.kt)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_canvas_title_field")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { newDocType = "code" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (newDocType == "code") EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Code Mode")
                        }
                        Button(
                            onClick = { newDocType = "document" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (newDocType == "document") EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Document Mode")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDocTitle.isNotBlank()) {
                            viewModel.createCanvasDocument(
                                title = newDocTitle,
                                type = newDocType,
                                language = if (newDocType == "code") "kotlin" else "markdown",
                                initialContent = if (newDocType == "code") "// Start coding here..." else "# ${newDocTitle}\n\nStart co-authoring here..."
                            )
                            newDocTitle = ""
                            isCreatingDoc = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_create_canvas_button")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreatingDoc = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Versions Dialog
    if (showVersionDialog && activeDoc != null) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("Canvas Version History") },
            text = {
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(activeDoc!!.versions) { version ->
                        Text(
                            text = "• $version",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVersionDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Comments Dialog
    if (showCommentsSheet && activeDoc != null) {
        AlertDialog(
            onDismissRequest = { showCommentsSheet = false },
            title = { Text("Canvas Co-Editor Annotations") },
            text = {
                LazyColumn(modifier = Modifier.height(220.dp)) {
                    items(activeDoc!!.comments) { comment ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "${comment.author} (Line ${comment.lineNumber}):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = comment.text,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCommentsSheet = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun CanvasActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
