package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val model: String = "GPT-4o",
    val customGptId: String? = null
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val conversationId: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String = "GPT-4o",
    val citations: List<Citation> = emptyList(),
    val isDeepResearch: Boolean = false,
    val deepResearchSteps: List<String> = emptyList(),
    val attachedImagePath: String? = null,
    val canvasDocId: String? = null
)

data class Citation(
    val title: String,
    val url: String,
    val snippet: String
)

data class CodeSnippet(
    val language: String,
    val code: String
)

@Entity(tableName = "canvas_documents")
data class CanvasDocument(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // "code" or "document"
    val language: String = "kotlin", // "kotlin", "python", "markdown", "javascript"
    val content: String,
    val versions: List<String> = emptyList(),
    val lastEditedAt: Long = System.currentTimeMillis(),
    val comments: List<CanvasComment> = emptyList()
)

data class CanvasComment(
    val id: String,
    val lineNumber: Int,
    val author: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "spaces")
data class Space(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val spaceType: String, // "living_doc", "interactive_sheet", "live_roadmap"
    val contentJson: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val collaborators: List<Collaborator> = emptyList(),
    val activityLog: List<SpaceActivity> = emptyList()
)

data class Collaborator(
    val id: String,
    val name: String,
    val role: String,
    val isAi: Boolean = false
)

data class SpaceActivity(
    val id: String,
    val authorName: String,
    val isAi: Boolean,
    val actionText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "autonomous_dots")
data class AutonomousDot(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val scheduleCronOrInterval: String, // e.g. "Every day at 8:00 AM", "Every 2 hours", "Continuous 24/7"
    val targetGoal: String,
    val connectedApps: List<String> = emptyList(), // "Gmail", "Calendar", "Slack", "GitHub", "Notion"
    val isActive: Boolean = true,
    val lastRunTime: Long = 0L,
    val nextRunTime: Long = 0L,
    val runHistory: List<DotRunLog> = emptyList()
)

data class DotRunLog(
    val id: String,
    val executedAt: Long,
    val status: String, // "COMPLETED", "IN_PROGRESS", "FAILED"
    val summary: String,
    val stepsCompleted: List<String>
)

@Entity(tableName = "user_memories")
data class UserMemory(
    @PrimaryKey val id: String,
    val key: String,
    val memoryText: String,
    val category: String = "General", // "Work", "Preferences", "Technical", "Style"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_gpts")
data class CustomGpt(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val systemPrompt: String,
    val author: String = "OpenAI",
    val category: String = "Productivity",
    val capabilities: List<String> = emptyList(),
    val samplePrompts: List<String> = emptyList(),
    val iconEmoji: String = "🤖"
)
