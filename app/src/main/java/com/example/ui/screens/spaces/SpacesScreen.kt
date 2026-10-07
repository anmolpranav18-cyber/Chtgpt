package com.example.ui.screens.spaces

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import org.json.JSONObject

@Composable
fun SpacesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spaces by viewModel.spaces.collectAsState()
    val activeSpaceId by viewModel.activeSpaceId.collectAsState()
    val activeSpace by viewModel.activeSpace.collectAsState()

    val context = LocalContext.current
    var isCreatingSpace by remember { mutableStateOf(false) }
    var newSpaceTitle by remember { mutableStateOf("") }
    var newSpaceDesc by remember { mutableStateOf("") }
    var newSpaceType by remember { mutableStateOf("living_doc") }
    var showActivityLog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
    ) {
        // Spaces Top Header
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
                            text = "Canvas Spaces",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Living documents & interactive sheets with real-time AI co-editing",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { isCreatingSpace = true },
                modifier = Modifier.testTag("create_space_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Space", tint = EmeraldPrimary)
            }
        }

        // Space selection tabs
        if (spaces.isNotEmpty()) {
            val selectedIndex = spaces.indexOfFirst { it.id == activeSpaceId }.coerceAtLeast(0)
            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary
            ) {
                spaces.forEachIndexed { index, space ->
                    Tab(
                        selected = index == selectedIndex,
                        onClick = { viewModel.selectSpace(space.id) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (space.spaceType == "interactive_sheet") Icons.Default.TableChart else Icons.Default.TaskAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (index == selectedIndex) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = space.title,
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

        activeSpace?.let { space ->
            // Collaborators Bar & AI Trigger Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Collaborator pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Collaborators",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    space.collaborators.forEach { collaborator ->
                        Surface(
                            color = if (collaborator.isAi) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = if (collaborator.isAi) "🤖 ${collaborator.name}" else "👤 ${collaborator.name.take(12)}",
                                fontSize = 10.sp,
                                fontWeight = if (collaborator.isAi) FontWeight.Bold else FontWeight.Normal,
                                color = if (collaborator.isAi) EmeraldLight else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // AI Co-Edit Action
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showActivityLog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Activity Feed",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.triggerAiSpaceUpdate()
                            Toast.makeText(context, "AI Co-Pilot synchronized Space dependencies", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("ai_space_co_edit_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ask AI to Update", fontSize = 11.sp)
                    }
                }
            }

            // Space Content Body
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = space.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (space.spaceType == "interactive_sheet") {
                    item {
                        InteractiveSheetContent(jsonString = space.contentJson)
                    }
                } else {
                    item {
                        LivingDocContent(jsonString = space.contentJson)
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No space selected. Tap '+' to create a collaborative space.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }

    // New Space Dialog
    if (isCreatingSpace) {
        AlertDialog(
            onDismissRequest = { isCreatingSpace = false },
            title = { Text("Create Collaborative Space") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newSpaceTitle,
                        onValueChange = { newSpaceTitle = it },
                        label = { Text("Space Name") },
                        modifier = Modifier.fillMaxWidth().testTag("new_space_title_field")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newSpaceDesc,
                        onValueChange = { newSpaceDesc = it },
                        label = { Text("Goal or Brief Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { newSpaceType = "living_doc" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (newSpaceType == "living_doc") EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Living Doc")
                        }
                        Button(
                            onClick = { newSpaceType = "interactive_sheet" },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (newSpaceType == "interactive_sheet") EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Interactive Sheet")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSpaceTitle.isNotBlank()) {
                            viewModel.createSpace(
                                title = newSpaceTitle,
                                description = newSpaceDesc.ifBlank { "Collaborative team space" },
                                spaceType = newSpaceType
                            )
                            newSpaceTitle = ""
                            newSpaceDesc = ""
                            isCreatingSpace = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_create_space_button")
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreatingSpace = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Activity Log Dialog
    if (showActivityLog && activeSpace != null) {
        AlertDialog(
            onDismissRequest = { showActivityLog = false },
            title = { Text("Live Space Activity Feed") },
            text = {
                LazyColumn(modifier = Modifier.height(240.dp)) {
                    items(activeSpace!!.activityLog) { activity ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (activity.isAi) EmeraldPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "${if (activity.isAi) "🤖" else "👤"} ${activity.authorName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activity.isAi) EmeraldLight else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = activity.actionText,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActivityLog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

data class SheetData(
    val columns: List<String>,
    val rows: List<List<String>>,
    val totalCost: String,
    val aiAnalysis: String
)

data class LivingDocData(
    val summary: String,
    val sections: List<DocSection>
)

data class DocSection(
    val title: String,
    val owner: String,
    val status: String,
    val aiRecommendation: String
)

@Composable
fun InteractiveSheetContent(jsonString: String) {
    val sheetData = remember(jsonString) {
        runCatching {
            val json = JSONObject(jsonString)
            val colList = mutableListOf<String>()
            json.optJSONArray("columns")?.let { arr ->
                for (i in 0 until arr.length()) colList.add(arr.getString(i))
            }
            val rowList = mutableListOf<List<String>>()
            json.optJSONArray("rows")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val rowArr = arr.getJSONArray(i)
                    val cells = mutableListOf<String>()
                    for (j in 0 until rowArr.length()) cells.add(rowArr.getString(j))
                    rowList.add(cells)
                }
            }
            SheetData(
                columns = colList,
                rows = rowList,
                totalCost = json.optString("totalMonthlyCost", ""),
                aiAnalysis = json.optString("aiAnalysis", "")
            )
        }.getOrNull()
    }

    if (sheetData == null) {
        Text("Invalid sheet format", fontSize = 12.sp, color = Color.Red)
        return
    }

    val scrollState = rememberScrollState()

    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.horizontalScroll(scrollState).padding(8.dp)) {
                // Header Row
                if (sheetData.columns.isNotEmpty()) {
                    Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp)) {
                        sheetData.columns.forEach { colName ->
                            Text(
                                text = colName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(130.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Data Rows
                sheetData.rows.forEach { rowCells ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        rowCells.forEach { cellText ->
                            Text(
                                text = cellText,
                                fontSize = 11.sp,
                                modifier = Modifier.width(130.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        if (sheetData.totalCost.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Total Monthly Cost: $${sheetData.totalCost}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }
        }

        if (sheetData.aiAnalysis.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Financial Analyst Insight: ${sheetData.aiAnalysis}",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun LivingDocContent(jsonString: String) {
    val docData = remember(jsonString) {
        runCatching {
            val json = JSONObject(jsonString)
            val secList = mutableListOf<DocSection>()
            json.optJSONArray("sections")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    secList.add(
                        DocSection(
                            title = obj.optString("title"),
                            owner = obj.optString("owner"),
                            status = obj.optString("status"),
                            aiRecommendation = obj.optString("aiRecommendation")
                        )
                    )
                }
            }
            LivingDocData(
                summary = json.optString("summary"),
                sections = secList
            )
        }.getOrNull()
    }

    if (docData == null) {
        Text("Invalid living doc format", fontSize = 12.sp, color = Color.Red)
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (docData.summary.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = docData.summary,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        docData.sections.forEach { sec ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sec.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            color = EmeraldPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = sec.status,
                                fontSize = 10.sp,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Owner: ${sec.owner}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (sec.aiRecommendation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sec.aiRecommendation,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
