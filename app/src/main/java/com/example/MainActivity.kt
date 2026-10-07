package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.CanvasDrawer
import com.example.ui.components.VoiceModeOverlay
import com.example.ui.screens.canvas.CanvasScreen
import com.example.ui.screens.chat.ChatScreen
import com.example.ui.screens.custom.CustomScreen
import com.example.ui.screens.dots.DotsScreen
import com.example.ui.screens.spaces.SpacesScreen
import com.example.ui.theme.CanvasDrawerBackground
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isVoiceModeOpen by viewModel.isVoiceModeOpen.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val activeConversationId by viewModel.activeConversationId.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Back button handling
    BackHandler(enabled = drawerState.isOpen || isVoiceModeOpen || currentTab != AppTab.CHAT) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (isVoiceModeOpen) {
            viewModel.closeVoiceMode()
        } else if (currentTab != AppTab.CHAT) {
            viewModel.setTab(AppTab.CHAT)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CanvasDrawerBackground
            ) {
                CanvasDrawer(
                    conversations = conversations,
                    activeConversationId = activeConversationId,
                    onSelectConversation = { convId ->
                        viewModel.selectConversation(convId)
                        viewModel.setTab(AppTab.CHAT)
                    },
                    onNewThread = {
                        viewModel.startNewConversation()
                        viewModel.setTab(AppTab.CHAT)
                    },
                    onOpenSpaces = {
                        viewModel.setTab(AppTab.SPACES)
                    },
                    onOpenSearch = {
                        viewModel.setTab(AppTab.CHAT)
                    },
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentTab) {
                AppTab.CHAT -> ChatScreen(
                    viewModel = viewModel,
                    onOpenDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
                AppTab.CANVAS -> CanvasScreen(
                    viewModel = viewModel,
                    onBack = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
                AppTab.SPACES -> SpacesScreen(
                    viewModel = viewModel,
                    onBack = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
                AppTab.DOTS -> DotsScreen(
                    viewModel = viewModel,
                    onBack = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
                AppTab.CUSTOM -> CustomScreen(
                    viewModel = viewModel,
                    onBack = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }

            // Animated Full-Screen Voice Mode Overlay
            AnimatedVisibility(
                visible = isVoiceModeOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                VoiceModeOverlay(
                    voiceModeManager = viewModel.voiceModeManager,
                    onClose = { viewModel.closeVoiceMode() },
                    onSubmitVoiceQuery = { query ->
                        viewModel.sendVoiceQuery(query)
                    }
                )
            }
        }
    }
}
