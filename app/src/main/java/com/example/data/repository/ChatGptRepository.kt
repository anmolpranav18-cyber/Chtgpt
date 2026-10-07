package com.example.data.repository

import com.example.data.local.ChatGptDatabase
import com.example.data.model.AutonomousDot
import com.example.data.model.CanvasComment
import com.example.data.model.CanvasDocument
import com.example.data.model.ChatMessage
import com.example.data.model.Citation
import com.example.data.model.Collaborator
import com.example.data.model.Conversation
import com.example.data.model.CustomGpt
import com.example.data.model.DotRunLog
import com.example.data.model.Space
import com.example.data.model.SpaceActivity
import com.example.data.model.UserMemory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatGptRepository(private val db: ChatGptDatabase) {

    private val chatDao = db.chatDao()
    private val canvasDao = db.canvasDao()
    private val spaceDao = db.spaceDao()
    private val dotDao = db.dotDao()
    private val memoryDao = db.memoryDao()
    private val customGptDao = db.customGptDao()

    val allConversations: Flow<List<Conversation>> = chatDao.getAllConversations()
    val allCanvasDocs: Flow<List<CanvasDocument>> = canvasDao.getAllCanvasDocuments()
    val allSpaces: Flow<List<Space>> = spaceDao.getAllSpaces()
    val allDots: Flow<List<AutonomousDot>> = dotDao.getAllDots()
    val allMemories: Flow<List<UserMemory>> = memoryDao.getAllMemories()
    val allCustomGpts: Flow<List<CustomGpt>> = customGptDao.getAllGpts()

    fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForConversation(convId)
    }

    fun getCanvasDocument(id: String): Flow<CanvasDocument?> {
        return canvasDao.getCanvasDocumentById(id)
    }

    fun getSpace(id: String): Flow<Space?> {
        return spaceDao.getSpaceById(id)
    }

    fun getDot(id: String): Flow<AutonomousDot?> {
        return dotDao.getDotById(id)
    }

    suspend fun createConversation(title: String, model: String = "GPT-4o", customGptId: String? = null): String = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val conv = Conversation(
            id = id,
            title = title,
            createdAt = System.currentTimeMillis(),
            lastUpdatedAt = System.currentTimeMillis(),
            model = model,
            customGptId = customGptId
        )
        chatDao.insertConversation(conv)
        id
    }

    suspend fun saveMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        chatDao.insertMessage(message)
        val conv = chatDao.getConversationById(message.conversationId)
        if (conv != null) {
            chatDao.updateConversation(conv.copy(lastUpdatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun deleteConversation(id: String) = withContext(Dispatchers.IO) {
        chatDao.deleteMessagesForConversation(id)
        chatDao.deleteConversation(id)
    }

    suspend fun saveCanvasDocument(doc: CanvasDocument) = withContext(Dispatchers.IO) {
        canvasDao.insertCanvasDocument(doc)
    }

    suspend fun deleteCanvasDocument(id: String) = withContext(Dispatchers.IO) {
        canvasDao.deleteCanvasDocument(id)
    }

    suspend fun saveSpace(space: Space) = withContext(Dispatchers.IO) {
        spaceDao.insertSpace(space)
    }

    suspend fun deleteSpace(id: String) = withContext(Dispatchers.IO) {
        spaceDao.deleteSpace(id)
    }

    suspend fun saveDot(dot: AutonomousDot) = withContext(Dispatchers.IO) {
        dotDao.insertDot(dot)
    }

    suspend fun toggleDot(id: String, active: Boolean) = withContext(Dispatchers.IO) {
        val dot = dotDao.getDotById(id).firstOrNull()
        if (dot != null) {
            dotDao.updateDot(dot.copy(isActive = active))
        }
    }

    suspend fun recordDotRun(id: String, log: DotRunLog) = withContext(Dispatchers.IO) {
        val dot = dotDao.getDotById(id).firstOrNull()
        if (dot != null) {
            val updatedHistory = listOf(log) + dot.runHistory.take(9)
            dotDao.updateDot(
                dot.copy(
                    lastRunTime = log.executedAt,
                    runHistory = updatedHistory
                )
            )
        }
    }

    suspend fun saveMemory(memory: UserMemory) = withContext(Dispatchers.IO) {
        memoryDao.insertMemory(memory)
    }

    suspend fun deleteMemory(id: String) = withContext(Dispatchers.IO) {
        memoryDao.deleteMemory(id)
    }

    suspend fun saveCustomGpt(gpt: CustomGpt) = withContext(Dispatchers.IO) {
        customGptDao.insertGpt(gpt)
    }

    suspend fun seedInitialDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingConvs = chatDao.getAllConversations().firstOrNull()
        if (existingConvs.isNullOrEmpty()) {
            seedConversations()
            seedCanvasDocs()
            seedSpaces()
            seedAutonomousDots()
            seedMemories()
            seedCustomGpts()
        }
    }

    private suspend fun seedConversations() {
        val now = System.currentTimeMillis()

        // Thread 1: Weekend in Copenhagen (2m ago)
        val convId1 = "conv_copenhagen"
        val conv1 = Conversation(
            id = convId1,
            title = "Weekend in Copenhagen",
            createdAt = now - 120000,
            lastUpdatedAt = now - 120000,
            model = "Thoughtful"
        )
        chatDao.insertConversation(conv1)
        chatDao.insertMessage(
            ChatMessage(
                id = "msg_cop_1",
                conversationId = convId1,
                role = "user",
                content = "Plan a slow-paced weekend in Copenhagen focusing on contemporary architecture, bakeries, and quiet canal strolls.",
                timestamp = now - 110000
            )
        )
        chatDao.insertMessage(
            ChatMessage(
                id = "msg_cop_2",
                conversationId = convId1,
                role = "assistant",
                content = """### Copenhagen 48-Hour Itinerary

**Saturday: Nyhavn, Christianshavn & Design**
- **Morning (08:30):** Cardamom bun & batch brew at *Hart Bageri* or *Lille Bakery*.
- **Mid-Morning (10:30):** Walk across the Inderhavnsbroen to Danish Architecture Center (BLOX).
- **Afternoon (14:00):** Quiet design exploration at Designmuseum Danmark & Frederiksstaden.
- **Evening (19:30):** Seasonal tasting dinner at *Høst* (Nordic farm-to-table).

**Sunday: Vesterbro, Lakes & Modernism**
- **Morning (09:00):** Run or stroll around the Sortedam Lake.
- **Midday (12:00):** Smørrebrød at *Aamanns 1921*.
- **Afternoon (15:00):** CopenHill ski-slope architecture & Torvehallerne glass market.""",
                timestamp = now - 100000,
                modelUsed = "Thoughtful"
            )
        )

        // Thread 2: Rewrite project proposal (1h ago)
        val convId2 = "conv_proposal"
        val conv2 = Conversation(
            id = convId2,
            title = "Rewrite project proposal",
            createdAt = now - 3600000,
            lastUpdatedAt = now - 3600000,
            model = "Thoughtful"
        )
        chatDao.insertConversation(conv2)
        chatDao.insertMessage(
            ChatMessage(
                id = "msg_prop_1",
                conversationId = convId2,
                role = "user",
                content = "Rewrite this project proposal to make the value proposition immediate and trim technical jargon.",
                timestamp = now - 3500000
            )
        )
        chatDao.insertMessage(
            ChatMessage(
                id = "msg_prop_2",
                conversationId = convId2,
                role = "assistant",
                content = """### Executive Project Proposal: Autonomous Canvas Platform

**Problem:** Knowledge workers spend 42% of their day switching between chat assistants, documentation workspaces, and task systems.
**Solution:** A unified living Canvas workspace combining co-authoring, deep research verification, and 24/7 autonomous agent automation.
**Expected Impact:**
1. **-65%** reduction in document co-authoring turnaround time.
2. **Zero** unverified citations through multi-agent consensus pipelines.""",
                timestamp = now - 3400000,
                modelUsed = "Thoughtful"
            )
        )

        // Thread 3: Explain vector databases (Tue)
        val convId3 = "conv_vectors"
        val conv3 = Conversation(
            id = convId3,
            title = "Explain vector databases",
            createdAt = now - 86400000 * 2,
            lastUpdatedAt = now - 86400000 * 2,
            model = "Thoughtful"
        )
        chatDao.insertConversation(conv3)
        chatDao.insertMessage(
            ChatMessage(
                id = "msg_vec_1",
                conversationId = convId3,
                role = "user",
                content = "Explain vector databases and cosine similarity simply.",
                timestamp = now - 86400000 * 2 + 1000
            )
        )
        chatDao.insertMessage(
            ChatMessage(
                id = "msg_vec_2",
                conversationId = convId3,
                role = "assistant",
                content = """A **Vector Database** stores data not by keywords or tables, but by mathematical meaning represented as multi-dimensional coordinates (embeddings).

- **Cosine Similarity:** Measures the angle between two embedding arrows in space. Smaller angle = closer semantic meaning.
- **HNSW (Hierarchical Navigable Small World):** A multi-layer graph index allowing millions of vectors to be queried in milliseconds.""",
                timestamp = now - 86400000 * 2 + 2000,
                modelUsed = "Thoughtful"
            )
        )

        // Thread 4: Dinner party menu ideas (Mon)
        val convId4 = "conv_dinner"
        val conv4 = Conversation(
            id = convId4,
            title = "Dinner party menu ideas",
            createdAt = now - 86400000 * 3,
            lastUpdatedAt = now - 86400000 * 3,
            model = "Thoughtful"
        )
        chatDao.insertConversation(conv4)
        chatDao.insertMessage(
            ChatMessage(
                id = "msg_din_1",
                conversationId = convId4,
                role = "user",
                content = "Give me a cozy autumn dinner party menu for 6 that can be prepped ahead of time.",
                timestamp = now - 86400000 * 3 + 1000
            )
        )
        chatDao.insertMessage(
            ChatMessage(
                id = "msg_din_2",
                conversationId = convId4,
                role = "assistant",
                content = """**Autumn Dinner Party Menu (Prep-Ahead)**
- **Starter:** Roasted butternut squash soup with crispy sage and brown butter sourdough croutons.
- **Main:** Braised short ribs with red wine reduction over creamy mascarpone polenta.
- **Dessert:** Warm poached pears in spiced cider with vanilla bean mascarpone.""",
                timestamp = now - 86400000 * 3 + 2000,
                modelUsed = "Thoughtful"
            )
        )
    }

    private suspend fun seedCanvasDocs() {
        val doc1 = CanvasDocument(
            id = "doc_worker_pool",
            title = "AsyncWorkerPool.kt",
            type = "code",
            language = "kotlin",
            content = """package com.example.concurrency

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

/**
 * Resilient Async Worker Pool with bounded queue backpressure
 * and supervisor job isolation for long-running AI pipelines.
 */
class AsyncWorkerPool(
    private val workerCount: Int = 4,
    private val queueCapacity: Int = 128
) {
    private val supervisorScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val taskChannel = Channel<suspend () -> Unit>(capacity = queueCapacity)
    
    private val _activeWorkers = MutableStateFlow(0)
    val activeWorkers = _activeWorkers.asStateFlow()
    
    private val processedTasks = AtomicInteger(0)

    init {
        repeat(workerCount) { workerId ->
            supervisorScope.launch {
                for (task in taskChannel) {
                    _activeWorkers.value += 1
                    try {
                        task.invoke()
                        processedTasks.incrementAndGet()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        println("[Worker #${"$"}workerId Error]: ${"$"}{e.message}")
                    } finally {
                        _activeWorkers.value -= 1
                    }
                }
            }
        }
    }

    suspend fun submit(task: suspend () -> Unit): Boolean {
        return try {
            taskChannel.send(task)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun shutdown() {
        taskChannel.close()
        supervisorScope.cancel()
    }
}
""".trimIndent(),
            versions = listOf(
                "Initial implementation with unbounded channel",
                "Added SupervisorScope and backpressure capacity limit (128)",
                "Integrated atomic telemetry and active worker StateFlow"
            ),
            lastEditedAt = System.currentTimeMillis() - 1200000,
            comments = listOf(
                CanvasComment(
                    id = "c1",
                    lineNumber = 16,
                    author = "AI Assistant",
                    text = "Using Channel(capacity = queueCapacity) prevents OOM when upstream producers emit bursts faster than workers consume.",
                    timestamp = System.currentTimeMillis() - 1500000
                ),
                CanvasComment(
                    id = "c2",
                    lineNumber = 29,
                    author = "AI Assistant",
                    text = "Explicitly re-throwing CancellationException ensures coroutine cancellation contracts remain intact.",
                    timestamp = System.currentTimeMillis() - 1400000
                )
            )
        )

        val doc2 = CanvasDocument(
            id = "doc_prd_ai_suite",
            title = "Autonomous Collaboration Protocol PRD",
            type = "document",
            language = "markdown",
            content = """# Autonomous Collaboration Protocol (ACP)
**Status:** In Co-Authoring | **Version:** 2.4 | **Target Launch:** Q4 2026

## 1. Executive Summary
The Autonomous Collaboration Protocol defines standard schemas for real-time human-AI co-authoring across Canvas workspaces, living Spaces, and 24/7 background Dots. 

## 2. Core Pillars
- **Zero-Latency State Synchronization:** Operational transformation (OT) and CRDT synchronization guaranteeing zero merge collisions across asynchronous device edits.
- **Multimodal Context Injection:** Real-time visual, audio, and code context ingested concurrently into the reasoning engine.
- **Persistent Shared Memory:** Semantic graphs persisting user preferences and technical decisions across independent sessions.

## 3. Security & Sandboxing
- Strict zero-trust permission models for autonomous background Dots.
- Granular permission scoping per connected third-party SaaS tool (e.g., Read-Only Calendar vs. Draft-Only Email).
""".trimIndent(),
            versions = listOf("Draft v1.0", "Revision v2.0 - Added Security Matrix", "Current v2.4"),
            lastEditedAt = System.currentTimeMillis() - 600000,
            comments = listOf(
                CanvasComment(
                    id = "c3",
                    lineNumber = 12,
                    author = "AI Co-Writer",
                    text = "Consider clarifying whether CRDT delta sync operates over WebSockets or local peer-to-peer meshes.",
                    timestamp = System.currentTimeMillis() - 500000
                )
            )
        )

        canvasDao.insertCanvasDocument(doc1)
        canvasDao.insertCanvasDocument(doc2)
    }

    private suspend fun seedSpaces() {
        val space1 = Space(
            id = "space_living_roadmap",
            title = "Q4 Product Strategy & Living Roadmap",
            description = "Shared living workspace co-edited by engineering leads and ChatGPT Co-Pilot",
            spaceType = "living_doc",
            contentJson = """{
  "summary": "Co-authored live roadmap tracking milestone readiness, risk mitigations, and cross-team dependencies.",
  "sections": [
    {
      "title": "Milestone A: Voice Mode Real-Time Latency < 250ms",
      "owner": "Audio Core Team",
      "status": "On Track",
      "aiRecommendation": "Optimizing Opus codec frame chunking reduced jitter by 18% in latest flight."
    },
    {
      "title": "Milestone B: Deep Research Source Verification Engine",
      "owner": "Research Agents Pod",
      "status": "In Review",
      "aiRecommendation": "Cross-referencing doi.org and semantic scholar metadata automated 94% of citation checks."
    },
    {
      "title": "Milestone C: Dots Autonomous Multi-Tool Orchestration",
      "owner": "Automations Guild",
      "status": "Testing",
      "aiRecommendation": "Added mandatory human-in-the-loop approval gates for external email dispatch."
    }
  ]
}""",
            updatedAt = System.currentTimeMillis() - 900000,
            collaborators = listOf(
                Collaborator("u1", "You (Lead Engineer)", "Editor", false),
                Collaborator("u2", "ChatGPT Co-Pilot", "Autonomous AI", true),
                Collaborator("u3", "Sarah Chen", "Product Manager", false)
            ),
            activityLog = listOf(
                SpaceActivity("a1", "ChatGPT Co-Pilot", true, "Auto-updated Milestone B risk status based on latest test benchmark.", System.currentTimeMillis() - 1800000),
                SpaceActivity("a2", "You (Lead Engineer)", false, "Added Opus chunking specification to Milestone A.", System.currentTimeMillis() - 3600000)
            )
        )

        val space2 = Space(
            id = "space_budget_sheet",
            title = "2026 AI Model Training & Token Budget",
            description = "Self-updating interactive financial and resource allocation sheet",
            spaceType = "interactive_sheet",
            contentJson = """{
  "columns": ["Workload", "Provider / Model", "Daily Tokens (M)", "Cost / 1M ($)", "Monthly Total ($)"],
  "rows": [
    ["Conversational Chat", "GPT-4o", "45.0", "2.50", "3,375.00"],
    ["Deep Research Pipelines", "o1-preview", "12.0", "15.00", "5,400.00"],
    ["Autonomous Dots 24/7", "Gemini 3.5 Flash", "85.0", "0.07", "178.50"],
    ["Canvas Co-Editing", "GPT-4o Mini", "30.0", "0.15", "135.00"]
  ],
  "totalMonthlyCost": "9,088.50",
  "aiAnalysis": "Switching background Dots routine heartbeats to Gemini 3.5 Flash reduces monthly burn rate by 42% with negligible accuracy trade-off."
}""",
            updatedAt = System.currentTimeMillis() - 1200000,
            collaborators = listOf(
                Collaborator("u1", "You (Lead Engineer)", "Owner", false),
                Collaborator("u2", "ChatGPT Co-Pilot", "AI Financial Analyst", true)
            ),
            activityLog = listOf(
                SpaceActivity("a3", "ChatGPT Co-Pilot", true, "Recomputed Monthly Total column and projected 42% cost savings.", System.currentTimeMillis() - 1200000)
            )
        )

        spaceDao.insertSpace(space1)
        spaceDao.insertSpace(space2)
    }

    private suspend fun seedAutonomousDots() {
        val dot1 = AutonomousDot(
            id = "dot_morning_digest",
            name = "Morning Executive Briefing & Calendar Prep",
            description = "Reviews upcoming meetings, summarizes priority emails, and drafts action items before 8:00 AM.",
            scheduleCronOrInterval = "Daily at 8:00 AM",
            targetGoal = "Zero missed high-priority emails, actionable meeting agendas ready 15m in advance.",
            connectedApps = listOf("Gmail", "Google Calendar", "Slack", "Notion"),
            isActive = true,
            lastRunTime = System.currentTimeMillis() - 14400000,
            nextRunTime = System.currentTimeMillis() + 43200000,
            runHistory = listOf(
                DotRunLog(
                    id = "run_1",
                    executedAt = System.currentTimeMillis() - 14400000,
                    status = "COMPLETED",
                    summary = "Scanned 34 unread messages, flagged 2 urgent PR review requests, and generated briefing notes for 10:30 AM Architecture Sync.",
                    stepsCompleted = listOf(
                        "Connected to Gmail via OAuth2: filtered VIP labels",
                        "Retrieved today's 4 calendar events and attendee list",
                        "Synthesized meeting prep doc in Notion: 'Q4 Infra Sync'",
                        "Pushed concise Slack DM summary to #personal-briefing"
                    )
                )
            )
        )

        val dot2 = AutonomousDot(
            id = "dot_pr_reviewer",
            name = "Repository Security & Lint Sentinel",
            description = "Monitors pull requests 24/7 for security antipatterns, token leaks, and Android architectural compliance.",
            scheduleCronOrInterval = "Continuous (Webhook & Event Driven)",
            targetGoal = "Prevent credential leakage and enforce Kotlin M3 guidelines automatically.",
            connectedApps = listOf("GitHub", "Slack", "Jira"),
            isActive = true,
            lastRunTime = System.currentTimeMillis() - 3600000,
            nextRunTime = System.currentTimeMillis() + 3600000,
            runHistory = listOf(
                DotRunLog(
                    id = "run_2",
                    executedAt = System.currentTimeMillis() - 3600000,
                    status = "COMPLETED",
                    summary = "Audited PR #142 'Add Canvas split pane': 0 secrets detected, suggested 1 CoroutineScope cancellation fix.",
                    stepsCompleted = listOf(
                        "Cloned diff patch for PR #142",
                        "Scanned for API key strings and regex matches (PASSED)",
                        "Performed static AST analysis on Compose remember blocks",
                        "Posted automated non-blocking comment on GitHub diff"
                    )
                )
            )
        )

        val dot3 = AutonomousDot(
            id = "dot_market_intel",
            name = "AI Model Benchmark & Competitor Radar",
            description = "Tracks HuggingFace leaderboards, arXiv preprints, and developer API pricing every 6 hours.",
            scheduleCronOrInterval = "Every 6 Hours",
            targetGoal = "Synthesize latest frontier model developments into actionable team updates.",
            connectedApps = listOf("Web Search", "Notion", "Slack"),
            isActive = true,
            lastRunTime = System.currentTimeMillis() - 21600000,
            nextRunTime = System.currentTimeMillis() + 7200000,
            runHistory = listOf(
                DotRunLog(
                    id = "run_3",
                    executedAt = System.currentTimeMillis() - 21600000,
                    status = "COMPLETED",
                    summary = "Analyzed 12 new arXiv preprints on speculative decoding and reasoning distillation.",
                    stepsCompleted = listOf(
                        "Queried arXiv cs.AI and cs.LG feeds",
                        "Filtered for terms 'reasoning', 'latency', 'distillation'",
                        "Extracted 3 top relevant papers with code implementations",
                        "Logged summaries to Team Space 'Living Roadmap'"
                    )
                )
            )
        )

        dotDao.insertDot(dot1)
        dotDao.insertDot(dot2)
        dotDao.insertDot(dot3)
    }

    private suspend fun seedMemories() {
        val m1 = UserMemory(
            id = "mem_1",
            key = "Developer Stack",
            memoryText = "Prefers modern Kotlin, Jetpack Compose, Material 3, and Coroutines over legacy patterns.",
            category = "Technical"
        )
        val m2 = UserMemory(
            id = "mem_2",
            key = "Response Style",
            memoryText = "Wants direct, concise answers with working code snippets first, followed by clear rationale.",
            category = "Preferences"
        )
        val m3 = UserMemory(
            id = "mem_3",
            key = "Current Project",
            memoryText = "Leading architecture for ChatGPT Android productivity suite with Voice Mode, Canvas, Spaces, and Dots.",
            category = "Work"
        )
        val m4 = UserMemory(
            id = "mem_4",
            key = "Time Zone & Locale",
            memoryText = "Based in Pacific Time (PT), standard 24h formatting, metric units preferred.",
            category = "General"
        )

        memoryDao.insertMemory(m1)
        memoryDao.insertMemory(m2)
        memoryDao.insertMemory(m3)
        memoryDao.insertMemory(m4)
    }

    private suspend fun seedCustomGpts() {
        val gpt1 = CustomGpt(
            id = "gpt_code_architect",
            name = "Code Architect Pro",
            description = "Specialized senior engineer for Kotlin, Jetpack Compose, backend systems, and high-performance algorithms.",
            systemPrompt = "You are Code Architect Pro, a world-class senior software engineer. Provide pristine, bug-free, type-safe code with optimal algorithmic complexity and modern Android M3 patterns.",
            category = "Engineering",
            capabilities = listOf("Code Analysis", "Canvas Integration", "Performance Profiling"),
            samplePrompts = listOf(
                "Refactor this flow to prevent recomposition spikes",
                "Design a fault-tolerant offline-first repository with Room",
                "Write a benchmark test for coroutine worker concurrency"
            ),
            iconEmoji = "💻"
        )

        val gpt2 = CustomGpt(
            id = "gpt_deep_researcher",
            name = "Deep Research Scholar",
            description = "Exhaustive multi-step researcher with cited whitepapers, bibliography synthesis, and rigorous verification.",
            systemPrompt = "You are Deep Research Scholar. Perform exhaustive multi-turn research, cross-reference credible sources, synthesize balanced technical trade-offs, and cite verified references.",
            category = "Research",
            capabilities = listOf("Web Search", "Multi-Step Synthesis", "Formal Citations"),
            samplePrompts = listOf(
                "Conduct deep research on quantum computing error correction",
                "Analyze state-of-the-art multimodal vision-language models",
                "Synthesize macroeconomic impact of agentic automation"
            ),
            iconEmoji = "🔬"
        )

        val gpt3 = CustomGpt(
            id = "gpt_dots_automator",
            name = "Dots Agent Automator",
            description = "Builds 24/7 background automation routines, webhook triggers, and cross-app integrations.",
            systemPrompt = "You are Dots Agent Automator. Formulate robust, resilient autonomous workflows with clear schedules, fail-safes, and OAuth SaaS integrations.",
            category = "Automation",
            capabilities = listOf("Dots Scheduling", "OAuth Connector", "Multi-Tool Orchestration"),
            samplePrompts = listOf(
                "Create a 24/7 inbox triage dot for VIP client tickets",
                "Configure automated GitHub pull request compliance sentinel",
                "Set up a recurring morning briefing with Calendar + Notion"
            ),
            iconEmoji = "⚡"
        )

        val gpt4 = CustomGpt(
            id = "gpt_canvas_editor",
            name = "Canvas Creative Writer",
            description = "Co-author articles, documentation, PRDs, and copy side-by-side with real-time feedback.",
            systemPrompt = "You are Canvas Creative Writer. Collaborate interactively on text, suggest targeted inline improvements, and adapt tone precisely.",
            category = "Writing",
            capabilities = listOf("Canvas Co-Writing", "Tone Adaptation", "Diff Review"),
            samplePrompts = listOf(
                "Draft an engineering PRD for live collaborative spaces",
                "Polish this technical release announcement",
                "Rewrite section 2 to be punchy and executive-friendly"
            ),
            iconEmoji = "✍️"
        )

        customGptDao.insertGpt(gpt1)
        customGptDao.insertGpt(gpt2)
        customGptDao.insertGpt(gpt3)
        customGptDao.insertGpt(gpt4)
    }
}
