package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.VoiceModeManager
import com.example.ai.VoiceState
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

@Composable
fun VoiceModeOverlay(
    voiceModeManager: VoiceModeManager,
    onClose: () -> Unit,
    onSubmitVoiceQuery: (String) -> Unit
) {
    val voiceState by voiceModeManager.voiceState.collectAsState()
    val amplitude by voiceModeManager.amplitude.collectAsState()
    val transcript by voiceModeManager.transcript.collectAsState()
    val aiResponse by voiceModeManager.aiResponse.collectAsState()
    val selectedVoice by voiceModeManager.selectedVoice.collectAsState()

    var customSpeechInput by remember { mutableStateOf("") }
    val voices = listOf("Breeze", "Cove", "Ember", "Juniper", "Sol")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.96f))
            .statusBarsPadding()
            .padding(16.dp)
            .testTag("voice_mode_overlay")
    ) {
        // Top controls: Close & Voice Persona Selection
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ChatGPT Voice Mode",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (voiceState) {
                            VoiceState.LISTENING -> "Listening to your voice..."
                            VoiceState.THINKING -> "Reasoning..."
                            VoiceState.SPEAKING -> "Speaking ($selectedVoice)..."
                            VoiceState.MUTED -> "Microphone muted"
                            else -> "Connecting audio pipeline..."
                        },
                        color = EmeraldLight,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2A2A2A), CircleShape)
                        .testTag("close_voice_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Voice Mode",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Voice Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                voices.forEach { voice ->
                    FilterChip(
                        selected = selectedVoice == voice,
                        onClick = { voiceModeManager.setVoice(voice) },
                        label = { Text(voice, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF252525),
                            labelColor = Color(0xFFCCCCCC)
                        )
                    )
                }
            }
        }

        // Center Pulsing Orb and Dynamic Transcripts
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PulsingVoiceOrb(
                voiceState = voiceState,
                amplitude = amplitude
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (transcript.isNotEmpty()) {
                Surface(
                    color = Color(0xFF262626),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = transcript,
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }

            if (aiResponse.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = aiResponse.take(180) + if (aiResponse.length > 180) "..." else "",
                    color = Color(0xFFAAAAAA),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        // Bottom Voice Simulation Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Quick speech prompt field (for testing in emulator without microphone hardware)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customSpeechInput,
                    onValueChange = { customSpeechInput = it },
                    placeholder = { Text("Speak or type test voice phrase...", fontSize = 12.sp, color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("voice_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF262626),
                        unfocusedContainerColor = Color(0xFF262626),
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = Color(0xFF404040),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (customSpeechInput.isNotBlank()) {
                            val text = customSpeechInput
                            customSpeechInput = ""
                            onSubmitVoiceQuery(text)
                        } else {
                            // Speak sample voice query
                            onSubmitVoiceQuery("Can you summarize the top three quantum computing breakthroughs this week?")
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(EmeraldPrimary, CircleShape)
                        .testTag("send_voice_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Spoken Query",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Mute & Mic controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { voiceModeManager.toggleMute() },
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            if (voiceState == VoiceState.MUTED) Color(0xFFEF4444) else Color(0xFF333333),
                            CircleShape
                        )
                        .testTag("mute_voice_button")
                ) {
                    Icon(
                        imageVector = if (voiceState == VoiceState.MUTED) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute or Unmute",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
