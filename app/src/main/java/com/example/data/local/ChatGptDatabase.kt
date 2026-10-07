package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.AutonomousDot
import com.example.data.model.CanvasDocument
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.CustomGpt
import com.example.data.model.Space
import com.example.data.model.UserMemory

@Database(
    entities = [
        Conversation::class,
        ChatMessage::class,
        CanvasDocument::class,
        Space::class,
        AutonomousDot::class,
        UserMemory::class,
        CustomGpt::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ChatGptTypeConverters::class)
abstract class ChatGptDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun canvasDao(): CanvasDao
    abstract fun spaceDao(): SpaceDao
    abstract fun dotDao(): AutonomousDotDao
    abstract fun memoryDao(): MemoryDao
    abstract fun customGptDao(): CustomGptDao

    companion object {
        @Volatile
        private var INSTANCE: ChatGptDatabase? = null

        fun getDatabase(context: Context): ChatGptDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChatGptDatabase::class.java,
                    "chatgpt_suite_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
