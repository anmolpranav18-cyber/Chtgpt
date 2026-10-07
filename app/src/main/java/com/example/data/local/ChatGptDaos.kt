package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AutonomousDot
import com.example.data.model.CanvasDocument
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.CustomGpt
import com.example.data.model.Space
import com.example.data.model.UserMemory
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY lastUpdatedAt DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversationById(id: String): Conversation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: Conversation)

    @Update
    suspend fun updateConversation(conversation: Conversation)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: String)

    @Query("SELECT * FROM chat_messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE conversationId = :convId")
    suspend fun deleteMessagesForConversation(convId: String)
}

@Dao
interface CanvasDao {
    @Query("SELECT * FROM canvas_documents ORDER BY lastEditedAt DESC")
    fun getAllCanvasDocuments(): Flow<List<CanvasDocument>>

    @Query("SELECT * FROM canvas_documents WHERE id = :id LIMIT 1")
    fun getCanvasDocumentById(id: String): Flow<CanvasDocument?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCanvasDocument(document: CanvasDocument)

    @Update
    suspend fun updateCanvasDocument(document: CanvasDocument)

    @Query("DELETE FROM canvas_documents WHERE id = :id")
    suspend fun deleteCanvasDocument(id: String)
}

@Dao
interface SpaceDao {
    @Query("SELECT * FROM spaces ORDER BY updatedAt DESC")
    fun getAllSpaces(): Flow<List<Space>>

    @Query("SELECT * FROM spaces WHERE id = :id LIMIT 1")
    fun getSpaceById(id: String): Flow<Space?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpace(space: Space)

    @Update
    suspend fun updateSpace(space: Space)

    @Query("DELETE FROM spaces WHERE id = :id")
    suspend fun deleteSpace(id: String)
}

@Dao
interface AutonomousDotDao {
    @Query("SELECT * FROM autonomous_dots ORDER BY nextRunTime ASC")
    fun getAllDots(): Flow<List<AutonomousDot>>

    @Query("SELECT * FROM autonomous_dots WHERE id = :id LIMIT 1")
    fun getDotById(id: String): Flow<AutonomousDot?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDot(dot: AutonomousDot)

    @Update
    suspend fun updateDot(dot: AutonomousDot)

    @Query("DELETE FROM autonomous_dots WHERE id = :id")
    suspend fun deleteDot(id: String)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM user_memories ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<UserMemory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: UserMemory)

    @Query("DELETE FROM user_memories WHERE id = :id")
    suspend fun deleteMemory(id: String)
}

@Dao
interface CustomGptDao {
    @Query("SELECT * FROM custom_gpts")
    fun getAllGpts(): Flow<List<CustomGpt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpt(gpt: CustomGpt)

    @Query("DELETE FROM custom_gpts WHERE id = :id")
    suspend fun deleteGpt(id: String)
}
