package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiService
import com.example.ai.VoiceModeManager
import com.example.data.local.ChatGptDatabase
import com.example.data.model.AutonomousDot
import com.example.data.model.CanvasComment
import com.example.data.model.CanvasDocument
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.CustomGpt
import com.example.data.model.DotRunLog
import com.example.data.model.Space
import com.example.data.model.SpaceActivity
import com.example.data.model.UserMemory
import com.example.data.repository.ChatGptRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab(val title: String) {
    CHAT("Chat"),
    CANVAS("Canvas"),
    SPACES("Spaces"),
    DOTS("Dots"),
    CUSTOM("Custom")
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ChatGptDatabase.getDatabase(application)
    val repository = ChatGptRepository(db)
    val aiService = AiService(application)
    val voiceModeManager = VoiceModeManager(application, viewModelScope)

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.CHAT)
    val currentTab = _currentTab.asStateFlow()

    // Conversations & Messages
    val conversations: StateFlow<List<Conversation>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId = _activeConversationId.asStateFlow()

    val currentMessages: StateFlow<List<ChatMessage>> = _activeConversationId.flatMapLatest { id ->
        if (id != null) repository.getMessagesForConversation(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat Settings & Toggles
    private val _selectedModel = MutableStateFlow("GPT-4o")
    val selectedModel = _selectedModel.asStateFlow()

    private val _isDeepResearchActive = MutableStateFlow(false)
    val isDeepResearchActive = _isDeepResearchActive.asStateFlow()

    private val _isWebSearchActive = MutableStateFlow(false)
    val isWebSearchActive = _isWebSearchActive.asStateFlow()

    private val _attachedImageUri = MutableStateFlow<Uri?>(null)
    val attachedImageUri = _attachedImageUri.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _deepResearchSteps = MutableStateFlow<List<String>>(emptyList())
    val deepResearchSteps = _deepResearchSteps.asStateFlow()

    // Voice Mode State
    private val _isVoiceModeOpen = MutableStateFlow(false)
    val isVoiceModeOpen = _isVoiceModeOpen.asStateFlow()

    // Canvas State
    val canvasDocuments: StateFlow<List<CanvasDocument>> = repository.allCanvasDocs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeCanvasDocId = MutableStateFlow<String?>(null)
    val activeCanvasDocId = _activeCanvasDocId.asStateFlow()

    val activeCanvasDoc: StateFlow<CanvasDocument?> = _activeCanvasDocId.flatMapLatest { id ->
        if (id != null) repository.getCanvasDocument(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Spaces State
    val spaces: StateFlow<List<Space>> = repository.allSpaces
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSpaceId = MutableStateFlow<String?>(null)
    val activeSpaceId = _activeSpaceId.asStateFlow()

    val activeSpace: StateFlow<Space?> = _activeSpaceId.flatMapLatest { id ->
        if (id != null) repository.getSpace(id) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Autonomous Dots State
    val autonomousDots: StateFlow<List<AutonomousDot>> = repository.allDots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isDotRunning = MutableStateFlow<String?>(null)
    val isDotRunning = _isDotRunning.asStateFlow()

    // Customization & Memory State
    val memories: StateFlow<List<UserMemory>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customGpts: StateFlow<List<CustomGpt>> = repository.allCustomGpts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeCustomGpt = MutableStateFlow<CustomGpt?>(null)
    val activeCustomGpt = _activeCustomGpt.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            // Select default conversation once seeded
            conversations.collect { list ->
                if (list.isNotEmpty() && _activeConversationId.value == null) {
                    _activeConversationId.value = list.first().id
                }
            }
        }
        viewModelScope.launch {
            canvasDocuments.collect { list ->
                if (list.isNotEmpty() && _activeCanvasDocId.value == null) {
                    _activeCanvasDocId.value = list.first().id
                }
            }
        }
        viewModelScope.launch {
            spaces.collect { list ->
                if (list.isNotEmpty() && _activeSpaceId.value == null) {
                    _activeSpaceId.value = list.first().id
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun selectConversation(id: String) {
        _activeConversationId.value = id
    }

    fun startNewConversation() {
        viewModelScope.launch {
            val title = if (_activeCustomGpt.value != null) "${_activeCustomGpt.value?.name} Chat" else "New Chat"
            val id = repository.createConversation(
                title = title,
                model = _selectedModel.value,
                customGptId = _activeCustomGpt.value?.id
            )
            _activeConversationId.value = id
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_activeConversationId.value == id) {
                _activeConversationId.value = conversations.value.firstOrNull { it.id != id }?.id
            }
        }
    }

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    fun toggleDeepResearch() {
        _isDeepResearchActive.value = !_isDeepResearchActive.value
        if (_isDeepResearchActive.value) {
            _isWebSearchActive.value = true
        }
    }

    fun toggleWebSearch() {
        _isWebSearchActive.value = !_isWebSearchActive.value
    }

    fun attachImage(uri: Uri?) {
        _attachedImageUri.value = uri
    }

    fun sendMessage(text: String) {
        if (text.isBlank() && _attachedImageUri.value == null) return

        val convId = _activeConversationId.value ?: return
        val currentImg = _attachedImageUri.value
        _attachedImageUri.value = null

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = convId,
            role = "user",
            content = text,
            timestamp = System.currentTimeMillis(),
            attachedImagePath = currentImg?.toString()
        )

        viewModelScope.launch {
            repository.saveMessage(userMessage)
            _isGenerating.value = true
            _deepResearchSteps.value = emptyList()

            try {
                val assistantMessage = aiService.generateResponse(
                    prompt = text,
                    conversationHistory = currentMessages.value,
                    model = _selectedModel.value,
                    isDeepResearch = _isDeepResearchActive.value,
                    isWebSearch = _isWebSearchActive.value,
                    imageUri = currentImg,
                    onResearchStep = { step ->
                        _deepResearchSteps.value = _deepResearchSteps.value + step
                    }
                ).copy(conversationId = convId)

                repository.saveMessage(assistantMessage)
            } finally {
                _isGenerating.value = false
                _isDeepResearchActive.value = false
            }
        }
    }

    // Voice Mode
    fun openVoiceMode() {
        _isVoiceModeOpen.value = true
        voiceModeManager.startSession()
    }

    fun closeVoiceMode() {
        _isVoiceModeOpen.value = false
        voiceModeManager.stopSession()
    }

    fun sendVoiceQuery(query: String) {
        voiceModeManager.submitSpokenQuery(query) { spokenText ->
            val response = aiService.generateResponse(
                prompt = spokenText,
                model = _selectedModel.value
            )
            // Also log to active conversation
            _activeConversationId.value?.let { convId ->
                repository.saveMessage(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        conversationId = convId,
                        role = "user",
                        content = "[Voice Mode] $spokenText",
                        timestamp = System.currentTimeMillis()
                    )
                )
                repository.saveMessage(
                    response.copy(
                        conversationId = convId,
                        content = "[Voice Mode Response] ${response.content}"
                    )
                )
            }
            response.content
        }
    }

    // Canvas
    fun selectCanvasDoc(id: String) {
        _activeCanvasDocId.value = id
        _currentTab.value = AppTab.CANVAS
    }

    fun updateCanvasContent(newContent: String) {
        val doc = activeCanvasDoc.value ?: return
        viewModelScope.launch {
            val updatedVersions = listOf("Edited at ${System.currentTimeMillis()}") + doc.versions.take(4)
            repository.saveCanvasDocument(
                doc.copy(
                    content = newContent,
                    lastEditedAt = System.currentTimeMillis(),
                    versions = updatedVersions
                )
            )
        }
    }

    fun createCanvasDocument(title: String, type: String, language: String, initialContent: String) {
        viewModelScope.launch {
            val newDoc = CanvasDocument(
                id = UUID.randomUUID().toString(),
                title = title,
                type = type,
                language = language,
                content = initialContent,
                lastEditedAt = System.currentTimeMillis()
            )
            repository.saveCanvasDocument(newDoc)
            _activeCanvasDocId.value = newDoc.id
        }
    }

    fun applyAiCanvasAction(actionType: String) {
        val doc = activeCanvasDoc.value ?: return
        viewModelScope.launch {
            val updatedContent = when (actionType) {
                "FIX_BUGS" -> {
                    "// [Canvas AI]: Resolved memory leaks and added null-safety guards\n" + doc.content
                }
                "ADD_COMMENTS" -> {
                    "/**\n * Auto-generated Canvas architectural documentation\n * Verified by ChatGPT Co-Pilot\n */\n" + doc.content
                }
                "OPTIMIZE" -> {
                    doc.content + "\n\n// Performance benchmark: Bounded channel reduces heap consumption by 35%"
                }
                "SHORTEN" -> {
                    doc.content.lines().filter { !it.trim().startsWith("//") }.joinToString("\n")
                }
                else -> doc.content
            }
            val newComment = CanvasComment(
                id = UUID.randomUUID().toString(),
                lineNumber = 1,
                author = "ChatGPT Canvas",
                text = "Applied '$actionType' transformation with automated syntax check.",
                timestamp = System.currentTimeMillis()
            )
            repository.saveCanvasDocument(
                doc.copy(
                    content = updatedContent,
                    lastEditedAt = System.currentTimeMillis(),
                    comments = listOf(newComment) + doc.comments
                )
            )
        }
    }

    // Spaces
    fun selectSpace(id: String) {
        _activeSpaceId.value = id
        _currentTab.value = AppTab.SPACES
    }

    fun createSpace(title: String, description: String, spaceType: String) {
        viewModelScope.launch {
            val newSpace = Space(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                spaceType = spaceType,
                contentJson = if (spaceType == "interactive_sheet") {
                    """{"columns":["Item","Status","Cost ($)"],"rows":[["Compute Cluster","Active","1,200.00"]],"totalMonthlyCost":"1,200.00"}"""
                } else {
                    """{"summary":"New collaborative workspace","sections":[{"title":"Overview","owner":"You","status":"Active"}]}"""
                },
                updatedAt = System.currentTimeMillis()
            )
            repository.saveSpace(newSpace)
            _activeSpaceId.value = newSpace.id
        }
    }

    fun triggerAiSpaceUpdate() {
        val current = activeSpace.value ?: return
        viewModelScope.launch {
            val newActivity = SpaceActivity(
                id = UUID.randomUUID().toString(),
                authorName = "ChatGPT Co-Pilot",
                isAi = true,
                actionText = "Cross-referenced project dependencies and optimized resource estimates.",
                timestamp = System.currentTimeMillis()
            )
            repository.saveSpace(
                current.copy(
                    updatedAt = System.currentTimeMillis(),
                    activityLog = listOf(newActivity) + current.activityLog
                )
            )
        }
    }

    // Autonomous Dots
    fun toggleDot(id: String, active: Boolean) {
        viewModelScope.launch {
            repository.toggleDot(id, active)
        }
    }

    fun triggerDotRun(dot: AutonomousDot) {
        _isDotRunning.value = dot.id
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            val log = DotRunLog(
                id = UUID.randomUUID().toString(),
                executedAt = System.currentTimeMillis(),
                status = "COMPLETED",
                summary = "Autonomous execution for '${dot.name}' completed with 0 errors across ${dot.connectedApps.joinToString(", ")}.",
                stepsCompleted = listOf(
                    "Connected to authentication endpoints for ${dot.connectedApps.joinToString(", ")}",
                    "Evaluated rule triggers against live event queues",
                    "Processed 18 items with automated filtering logic",
                    "Logged telemetry record and sent push digest"
                )
            )
            repository.recordDotRun(dot.id, log)
            _isDotRunning.value = null
        }
    }

    fun createDot(name: String, description: String, schedule: String, targetGoal: String, apps: List<String>) {
        viewModelScope.launch {
            val dot = AutonomousDot(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                scheduleCronOrInterval = schedule,
                targetGoal = targetGoal,
                connectedApps = apps,
                isActive = true,
                lastRunTime = System.currentTimeMillis(),
                nextRunTime = System.currentTimeMillis() + 86400000
            )
            repository.saveDot(dot)
        }
    }

    // Memories & Custom Instructions
    fun addMemory(key: String, text: String, category: String) {
        viewModelScope.launch {
            val mem = UserMemory(
                id = UUID.randomUUID().toString(),
                key = key,
                memoryText = text,
                category = category,
                createdAt = System.currentTimeMillis()
            )
            repository.saveMemory(mem)
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun selectCustomGpt(gpt: CustomGpt?) {
        _activeCustomGpt.value = gpt
        if (gpt != null) {
            _selectedModel.value = gpt.name
        }
    }

    fun createCustomGpt(name: String, description: String, instructions: String, category: String, emoji: String) {
        viewModelScope.launch {
            val gpt = CustomGpt(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                systemPrompt = instructions,
                category = category,
                author = "You",
                iconEmoji = emoji,
                samplePrompts = listOf("Get started with $name", "Explain best practices", "Review my project")
            )
            repository.saveCustomGpt(gpt)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceModeManager.destroy()
    }
}
