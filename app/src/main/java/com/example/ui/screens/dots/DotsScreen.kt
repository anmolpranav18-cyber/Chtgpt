package com.example.ui.screens.dots

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.AutonomousDot
import com.example.ui.MainViewModel
import com.example.ui.theme.AgentPurple
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DotsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dots by viewModel.autonomousDots.collectAsState()
    val runningDotId by viewModel.isDotRunning.collectAsState()
    val context = LocalContext.current

    var isCreatingDot by remember { mutableStateOf(false) }
    var newDotName by remember { mutableStateOf("") }
    var newDotDesc by remember { mutableStateOf("") }
    var newDotGoal by remember { mutableStateOf("") }
    var newDotSchedule by remember { mutableStateOf("Daily at 8:00 AM") }
    var selectedApps by remember { mutableStateOf(setOf("Gmail", "Google Calendar")) }

    var inspectDot by remember { mutableStateOf<AutonomousDot?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
    ) {
        // Header
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
                                .background(AgentPurple)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Canvas Dots",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "24/7 autonomous AI assistants managing recurring tasks & integrations",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { isCreatingDot = true },
                modifier = Modifier.testTag("create_dot_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Dot", tint = AgentPurple)
            }
        }

        // 24/7 Agent Heartbeat Status Banner
        val activeCount = dots.count { it.isActive }
        Surface(
            color = AgentPurple.copy(alpha = 0.12f),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgentPurple.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AgentPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Autonomous Agent Core Active",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$activeCount recurring dots armed • Continuous background event listener connected",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // List of Dots
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(dots, key = { it.id }) { dot ->
                val isRunning = runningDotId == dot.id

                Card(
                    modifier = Modifier.fillMaxWidth().testTag("dot_card_${dot.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (dot.isActive) EmeraldPrimary else Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dot.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Switch(
                                checked = dot.isActive,
                                onCheckedChange = { active ->
                                    viewModel.toggleDot(dot.id, active)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AgentPurple
                                ),
                                modifier = Modifier.testTag("dot_toggle_${dot.id}")
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dot.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Schedule & Target Goal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = AgentPurple, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = dot.scheduleCronOrInterval,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AgentPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Connected App Badges
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            dot.connectedApps.forEach { app ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "🔌 $app",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions Row: Trigger Run Now & View Logs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { inspectDot = dot }
                            ) {
                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Execution Logs (${dot.runHistory.size})", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.triggerDotRun(dot)
                                    Toast.makeText(context, "Executing autonomous dot '${dot.name}'", Toast.LENGTH_SHORT).show()
                                },
                                enabled = !isRunning && dot.isActive,
                                colors = ButtonDefaults.buttonColors(containerColor = AgentPurple),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("trigger_dot_button_${dot.id}")
                            ) {
                                if (isRunning) {
                                    CircularProgressIndicator(modifier = Modifier.size(12.dp), color = Color.White, strokeWidth = 1.5.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Running...", fontSize = 11.sp)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Run Now", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create New Dot Dialog
    if (isCreatingDot) {
        val availableApps = listOf("Gmail", "Google Calendar", "Slack", "GitHub", "Notion", "Jira", "Webhooks")
        AlertDialog(
            onDismissRequest = { isCreatingDot = false },
            title = { Text("Create Autonomous Dot") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newDotName,
                        onValueChange = { newDotName = it },
                        label = { Text("Dot Name (e.g., Daily Inbox Triage)") },
                        modifier = Modifier.fillMaxWidth().testTag("new_dot_name_field")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDotDesc,
                        onValueChange = { newDotDesc = it },
                        label = { Text("Task Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDotGoal,
                        onValueChange = { newDotGoal = it },
                        label = { Text("Objective / Success Metric") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDotSchedule,
                        onValueChange = { newDotSchedule = it },
                        label = { Text("Schedule (e.g. Daily at 8:00 AM, Continuous)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Connected App Integrations:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        availableApps.forEach { app ->
                            val isSelected = selectedApps.contains(app)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedApps = if (isSelected) selectedApps - app else selectedApps + app
                                },
                                label = { Text(app, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newDotName.isNotBlank()) {
                            viewModel.createDot(
                                name = newDotName,
                                description = newDotDesc.ifBlank { "Autonomous background automation" },
                                schedule = newDotSchedule,
                                targetGoal = newDotGoal.ifBlank { "Automate recurring workflow" },
                                apps = selectedApps.toList()
                            )
                            newDotName = ""
                            newDotDesc = ""
                            isCreatingDot = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_create_dot_button")
                ) {
                    Text("Deploy Dot")
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreatingDot = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Inspect Logs Dialog
    inspectDot?.let { dot ->
        AlertDialog(
            onDismissRequest = { inspectDot = null },
            title = { Text("Logs: ${dot.name}") },
            text = {
                if (dot.runHistory.isEmpty()) {
                    Text("No runs recorded yet. Tap 'Run Now' to execute.", fontSize = 12.sp)
                } else {
                    LazyColumn(modifier = Modifier.height(280.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(dot.runHistory) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = log.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                        Text(
                                            text = "Timestamp: ${log.executedAt}",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = log.summary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    log.stepsCompleted.forEach { step ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 1.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(11.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(step, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { inspectDot = null }) {
                    Text("Close")
                }
            }
        )
    }
}
