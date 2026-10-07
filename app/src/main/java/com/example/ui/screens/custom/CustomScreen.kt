package com.example.ui.screens.custom

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.CustomGpt
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val memories by viewModel.memories.collectAsState()
    val customGpts by viewModel.customGpts.collectAsState()
    val activeGpt by viewModel.activeCustomGpt.collectAsState()
    val context = LocalContext.current

    var selectedSubTab by remember { mutableIntStateOf(0) }
    var isAddingMemory by remember { mutableStateOf(false) }
    var newMemKey by remember { mutableStateOf("") }
    var newMemText by remember { mutableStateOf("") }
    var newMemCategory by remember { mutableStateOf("Preferences") }

    var isCreatingGpt by remember { mutableStateOf(false) }
    var newGptName by remember { mutableStateOf("") }
    var newGptDesc by remember { mutableStateOf("") }
    var newGptInstructions by remember { mutableStateOf("") }
    var newGptEmoji by remember { mutableStateOf("⚡") }

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
                                .background(EmeraldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Canvas Persona",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Manage cross-chat memories, custom instructions, and Custom GPTs",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Sub Tabs: Memory Bank, Custom GPTs, Custom Instructions
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Memory Bank (${memories.size})", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Custom GPTs (${customGpts.size})", fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Instructions", fontSize = 12.sp) }
            )
        }

        when (selectedSubTab) {
            0 -> {
                // Memory Bank View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ChatGPT remembers these details across all chats:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { isAddingMemory = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("add_memory_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Memory", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(memories, key = { it.id }) { memory ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = EmeraldPrimary.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = memory.category,
                                                    fontSize = 10.sp,
                                                    color = EmeraldPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = memory.key,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = memory.memoryText,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.deleteMemory(memory.id)
                                            Toast.makeText(context, "Memory deleted", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Forget",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Custom GPTs Directory
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Specialized AI personas with unique system prompts:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { isCreatingGpt = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("create_custom_gpt_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Build GPT", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(customGpts, key = { it.id }) { gpt ->
                            val isActive = activeGpt?.id == gpt.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectCustomGpt(if (isActive) null else gpt)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isActive) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isActive) 1.5.dp else 1.dp,
                                    if (isActive) EmeraldPrimary else MaterialTheme.colorScheme.outline
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(gpt.iconEmoji, fontSize = 24.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = gpt.name,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "By ${gpt.author} • ${gpt.category}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        if (isActive) {
                                            Surface(
                                                color = EmeraldPrimary,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Active", fontSize = 10.sp, color = Color.White)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = gpt.description,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        gpt.capabilities.forEach { cap ->
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = cap,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = {
                                                viewModel.selectCustomGpt(gpt)
                                                viewModel.startNewConversation()
                                                viewModel.setTab(AppTab.CHAT)
                                            }
                                        ) {
                                            Text("Start Chatting with ${gpt.name}", fontSize = 11.sp, color = EmeraldPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Custom Instructions Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Customize how ChatGPT responds to you across all conversations.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "What would you like ChatGPT to know about you to provide better responses?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = "Principal Android & Systems Engineer leading high-scale mobile applications. Based in PT. Specializes in Kotlin, Jetpack Compose, Coroutines, and Multi-Agent Autonomous systems.",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "How would you like ChatGPT to respond?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = "Direct, production-grade Kotlin snippets first with type safety and error boundaries. Provide clear architectural trade-offs. Cite empirical benchmarks when available.",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 4,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Custom instructions saved & synchronized", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Custom Instructions")
                    }
                }
            }
        }
    }

    // Add Memory Dialog
    if (isAddingMemory) {
        AlertDialog(
            onDismissRequest = { isAddingMemory = false },
            title = { Text("Remember Detail") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newMemKey,
                        onValueChange = { newMemKey = it },
                        label = { Text("Topic (e.g. Tone Preference, Stack)") },
                        modifier = Modifier.fillMaxWidth().testTag("new_memory_key_field")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newMemText,
                        onValueChange = { newMemText = it },
                        label = { Text("What to remember") },
                        modifier = Modifier.fillMaxWidth().testTag("new_memory_text_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMemKey.isNotBlank() && newMemText.isNotBlank()) {
                            viewModel.addMemory(newMemKey, newMemText, newMemCategory)
                            newMemKey = ""
                            newMemText = ""
                            isAddingMemory = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_add_memory_button")
                ) {
                    Text("Save Memory")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingMemory = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Create Custom GPT Dialog
    if (isCreatingGpt) {
        AlertDialog(
            onDismissRequest = { isCreatingGpt = false },
            title = { Text("Build Custom GPT") },
            text = {
                Column {
                    Row {
                        OutlinedTextField(
                            value = newGptEmoji,
                            onValueChange = { newGptEmoji = it },
                            label = { Text("Icon") },
                            modifier = Modifier.width(70.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = newGptName,
                            onValueChange = { newGptName = it },
                            label = { Text("GPT Name") },
                            modifier = Modifier.fillMaxWidth().testTag("new_gpt_name_field")
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newGptDesc,
                        onValueChange = { newGptDesc = it },
                        label = { Text("Short Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newGptInstructions,
                        onValueChange = { newGptInstructions = it },
                        label = { Text("System Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGptName.isNotBlank()) {
                            viewModel.createCustomGpt(
                                name = newGptName,
                                description = newGptDesc.ifBlank { "Custom specialized assistant" },
                                instructions = newGptInstructions.ifBlank { "Be a helpful tailored expert." },
                                category = "Custom",
                                emoji = newGptEmoji.ifBlank { "🤖" }
                            )
                            newGptName = ""
                            newGptDesc = ""
                            newGptInstructions = ""
                            isCreatingGpt = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_create_gpt_button")
                ) {
                    Text("Publish GPT")
                }
            },
            dismissButton = {
                TextButton(onClick = { isCreatingGpt = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
