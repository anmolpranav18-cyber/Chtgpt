package com.example

import com.example.data.model.AutonomousDot
import com.example.data.model.CanvasDocument
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.CustomGpt
import com.example.data.model.DotRunLog
import com.example.data.model.Space
import com.example.data.model.UserMemory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatGptSuiteTest {

    @Test
    fun testConversationAndMessageModel() {
        val conv = Conversation(
            id = "test_conv",
            title = "Quantum Reasoning",
            model = "o1-preview"
        )
        assertEquals("test_conv", conv.id)
        assertEquals("o1-preview", conv.model)

        val msg = ChatMessage(
            id = "test_msg",
            conversationId = conv.id,
            role = "assistant",
            content = "Synthesizing quantum error correction steps...",
            isDeepResearch = true,
            deepResearchSteps = listOf("Analyzed journals", "Verified fidelities")
        )
        assertTrue(msg.isDeepResearch)
        assertEquals(2, msg.deepResearchSteps.size)
    }

    @Test
    fun testCanvasDocumentModel() {
        val doc = CanvasDocument(
            id = "canvas_1",
            title = "WorkerPool.kt",
            type = "code",
            language = "kotlin",
            content = "class WorkerPool"
        )
        assertEquals("WorkerPool.kt", doc.title)
        assertEquals("kotlin", doc.language)
    }

    @Test
    fun testAutonomousDotAndRunLog() {
        val log = DotRunLog(
            id = "log_1",
            executedAt = 1000L,
            status = "COMPLETED",
            summary = "Inbox audited",
            stepsCompleted = listOf("Connected to Gmail", "Processed 12 VIP emails")
        )
        val dot = AutonomousDot(
            id = "dot_1",
            name = "Morning Digest",
            description = "Calendar & email prep",
            scheduleCronOrInterval = "Daily at 8:00 AM",
            targetGoal = "Zero missed threads",
            connectedApps = listOf("Gmail", "Calendar"),
            runHistory = listOf(log)
        )
        assertTrue(dot.isActive)
        assertEquals(1, dot.runHistory.size)
        assertEquals("COMPLETED", dot.runHistory.first().status)
    }

    @Test
    fun testMemoryAndCustomGpt() {
        val memory = UserMemory(
            id = "mem_1",
            key = "Style",
            memoryText = "Direct concise code",
            category = "Preferences"
        )
        assertEquals("Preferences", memory.category)

        val gpt = CustomGpt(
            id = "gpt_1",
            name = "Code Copilot",
            description = "Senior Kotlin Dev",
            systemPrompt = "You are a Kotlin expert.",
            capabilities = listOf("Canvas Integration", "Refactoring")
        )
        assertNotNull(gpt)
        assertEquals("Code Copilot", gpt.name)
        assertTrue(gpt.capabilities.contains("Canvas Integration"))
    }
}
