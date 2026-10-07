package com.example.ui.screens.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import com.example.ui.components.MessageBubble
import com.example.ui.theme.CanvasBlueAccent
import com.example.ui.theme.CanvasLightBackground
import com.example.ui.theme.CanvasLightBorder
import com.example.ui.theme.CanvasLightSurface
import com.example.ui.theme.CanvasLimeAccent
import com.example.ui.theme.CanvasLimeOnColor
import com.example.ui.theme.CanvasTextMuted
import com.example.ui.theme.CanvasTextPrimary
import com.example.ui.theme.CanvasTextSecondary
import com.example.ui.theme.ResearchBlue

@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.currentMessages.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val isDeepResearch by viewModel.isDeepResearchActive.collectAsState()
    val attachedImageUri by viewModel.attachedImageUri.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val deepResearchSteps by viewModel.deepResearchSteps.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showModelMenu by remember { mutableStateOf(false) }
    var showModePillMenu by remember { mutableStateOf(false) }
    var selectedThinkingMode by remember { mutableStateOf("Thoughtful") }

    val listState = rememberLazyListState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.attachImage(uri)
    }

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasLightBackground)
            .statusBarsPadding()
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP BAR: Hamburger Menu (☰), Center Dropdown (Canvas • Bright ⌄), Right User Profile (👤)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hamburger Menu Button
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("open_sidebar_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Sidebar",
                        tint = CanvasTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Dropdown Pill: Canvas • Bright ⌄
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showModelMenu = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("canvas_mode_header_pill"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Canvas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CanvasTextPrimary
                        )
                        Text(
                            text = " • ",
                            fontSize = 14.sp,
                            color = CanvasTextMuted
                        )
                        Text(
                            text = if (selectedModel.contains("o1")) "Deep" else "Bright",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = CanvasTextSecondary
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select Mode",
                            tint = CanvasTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showModelMenu,
                        onDismissRequest = { showModelMenu = false }
                    ) {
                        listOf("Canvas • Bright", "Canvas • Thoughtful", "Canvas • Deep Research", "Canvas • o1 Reasoning").forEach { modeLabel ->
                            DropdownMenuItem(
                                text = { Text(modeLabel, fontSize = 13.sp) },
                                onClick = {
                                    if (modeLabel.contains("Deep")) {
                                        viewModel.toggleDeepResearch()
                                    }
                                    showModelMenu = false
                                }
                            )
                        }
                    }
                }

                // Right User Avatar Profile Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF2FE))
                        .clickable { viewModel.setTab(com.example.ui.AppTab.CUSTOM) }
                        .testTag("user_profile_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF4A67B3),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // MAIN CONTENT BODY: Hero "Good morning, Alex" state OR Active thread messages
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty() && !isGenerating) {
                    val heroScrollState = rememberScrollState()
                    // BLANK CANVAS HERO STATE (matching Screenshot 2 precisely!)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(heroScrollState)
                            .padding(horizontal = 22.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Section rule: —— A BLANK PAGE, FULL OF POSSIBILITY ——
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFD6DAE0),
                                thickness = 1.dp
                            )
                            Text(
                                text = "A BLANK PAGE, FULL OF POSSIBILITY",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7A808C),
                                letterSpacing = 2.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFD6DAE0),
                                thickness = 1.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Large Headline: Good morning, Alex.
                        Text(
                            text = "Good morning,\nAlex.",
                            fontFamily = FontFamily.Serif,
                            fontSize = 44.sp,
                            lineHeight = 48.sp,
                            fontWeight = FontWeight.Normal,
                            color = CanvasTextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Subtitle: What would you like to make sense of today?
                        Text(
                            text = "What would you like to make sense of\ntoday?",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 19.sp,
                            lineHeight = 26.sp,
                            color = Color(0xFF6B717B),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(34.dp))

                        // Prompt Card: 01 PLAN -> Create a focused week
                        Surface(
                            color = CanvasLightSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CanvasLightBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    inputText = "Create a focused week plan"
                                    viewModel.sendMessage("Create a focused week plan for deep work and architectural reviews.")
                                }
                                .testTag("plan_card_01")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "01\nPLAN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CanvasBlueAccent,
                                        letterSpacing = 1.sp,
                                        lineHeight = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Create a focused week",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CanvasTextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF1F3F5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Execute",
                                        tint = CanvasTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Prompt Card: 02 BUILD -> Build a complete app in Canvas
                        Surface(
                            color = CanvasLightSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CanvasLightBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.sendMessage("Build a complete Habit & Task Tracker app in Jetpack Compose")
                                }
                                .testTag("plan_card_02")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "02\nBUILD",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF108A4D),
                                        letterSpacing = 1.sp,
                                        lineHeight = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Build a complete app in Canvas",
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CanvasTextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CanvasLimeAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Build App",
                                        tint = CanvasLimeOnColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // ACTIVE THREAD VIEW
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            MessageBubble(
                                message = msg,
                                onOpenCanvasDoc = { docId ->
                                    viewModel.selectCanvasDoc(docId)
                                }
                            )
                        }

                        if (isGenerating) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = if (isDeepResearch) ResearchBlue else CanvasTextPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (isDeepResearch) {
                                            deepResearchSteps.lastOrNull() ?: "Conducting Deep Research..."
                                        } else {
                                            "Canvas is thinking..."
                                        },
                                        fontSize = 12.5.sp,
                                        color = CanvasTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ATTACHMENT PREVIEW IF ANY
            AnimatedVisibility(visible = attachedImageUri != null) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .size(56.dp)
                ) {
                    AsyncImage(
                        model = attachedImageUri,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, CanvasLightBorder, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { viewModel.attachImage(null) },
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(10.dp))
                    }
                }
            }

            // BOTTOM FLOATING INPUT CARD CONTAINER (Screenshot 2)
            Surface(
                color = CanvasLightSurface,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CanvasLightBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("canvas_input_card")
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    // Text Input Area
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Start with a question, an idea, or a half-formed thought",
                                fontSize = 14.sp,
                                color = CanvasTextMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("canvas_main_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = CanvasTextPrimary,
                            unfocusedTextColor = CanvasTextPrimary
                        ),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Bottom Bar inside Input Card: Paperclip, Mode Pill (● Thoughtful ⌄), Voice Mode, Send Button (→)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Paperclip attachment button
                            IconButton(
                                onClick = { photoPickerLauncher.launch("image/*") },
                                modifier = Modifier.size(34.dp).testTag("input_attachment_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Attach file",
                                    tint = CanvasTextSecondary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Mode Pill: ● Thoughtful ⌄
                            Box {
                                Surface(
                                    color = Color(0xFFF1F3F5),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .clickable { showModePillMenu = true }
                                        .testTag("mode_pill_selector")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(CanvasBlueAccent)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = selectedThinkingMode,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = CanvasTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = CanvasTextSecondary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showModePillMenu,
                                    onDismissRequest = { showModePillMenu = false }
                                ) {
                                    listOf("Thoughtful", "Deep Research", "Canvas Co-Write", "Quick Chat").forEach { mode ->
                                        DropdownMenuItem(
                                            text = { Text(mode, fontSize = 12.5.sp) },
                                            onClick = {
                                                selectedThinkingMode = mode
                                                if (mode == "Deep Research") viewModel.toggleDeepResearch()
                                                showModePillMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Voice Mode Button
                            IconButton(
                                onClick = { viewModel.openVoiceMode() },
                                modifier = Modifier.size(34.dp).testTag("input_voice_mode_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Voice Mode",
                                    tint = CanvasTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            // Send Circular Button: →
                            val canSend = (inputText.isNotBlank() || attachedImageUri != null) && !isGenerating
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (canSend) CanvasTextPrimary else Color(0xFFF1F3F5)
                                    )
                                    .clickable(enabled = canSend) {
                                        val textToSend = inputText
                                        inputText = ""
                                        viewModel.sendMessage(textToSend)
                                    }
                                    .testTag("canvas_send_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Send",
                                    tint = if (canSend) Color.White else CanvasTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Disclaimer text: Canvas can make mistakes. Keep your judgment in the loop.
            Text(
                text = "Canvas can make mistakes. Keep your judgment in the loop.",
                fontSize = 11.sp,
                color = CanvasTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )
        }
    }
}
