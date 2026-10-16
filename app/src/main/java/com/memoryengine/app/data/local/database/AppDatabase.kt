package com.memoryengine.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.memoryengine.app.data.local.dao.AssociationDao
import com.memoryengine.app.data.local.dao.DocumentDao
import com.memoryengine.app.data.local.dao.MemoryChunkDao
import com.memoryengine.app.data.local.dao.RecallPromptDao
import com.memoryengine.app.data.local.entity.Association
import com.memoryengine.app.data.local.entity.Document
import com.memoryengine.app.data.local.entity.MemoryChunk
import com.memoryengine.app.data.local.entity.RecallPrompt

@Database(
    entities = [
        Document::class,
        MemoryChunk::class,
        RecallPrompt::class,
        Association::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun documentDao(): DocumentDao
    abstract fun memoryChunkDao(): MemoryChunkDao
    abstract fun recallPromptDao(): RecallPromptDao
    abstract fun associationDao(): AssociationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "memory_engine.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
