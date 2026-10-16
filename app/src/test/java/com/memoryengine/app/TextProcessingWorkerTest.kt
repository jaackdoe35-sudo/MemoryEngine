package com.memoryengine.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.memoryengine.app.data.local.database.AppDatabase
import com.memoryengine.app.data.local.entity.Document
import com.memoryengine.app.data.remote.service.AiService
import com.memoryengine.app.data.remote.service.RecallPromptData
import com.memoryengine.app.worker.TextChunker
import com.memoryengine.app.worker.TextProcessingWorker
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextProcessingWorkerTest {

    private lateinit var database: AppDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `TextChunker splits long text by paragraphs`() {
        val paragraph1 = "Paragraph 1 with multiple words about active recall and memory retention techniques."
        val paragraph2 = "Paragraph 2 discussing spaced repetition algorithm SM-2 and optimal review intervals."
        val fullText = "$paragraph1\n\n$paragraph2"

        val chunks = TextChunker.chunkText(fullText, maxWordsPerChunk = 10)

        assertEquals(2, chunks.size)
        assertEquals(paragraph1, chunks[0])
        assertEquals(paragraph2, chunks[1])
    }

    @Test
    fun `TextProcessingWorker stores chunks, prompts, and associations in database`() = runBlocking {
        val docId = "doc-123"
        val doc = Document(id = docId, title = "Brain Mechanics", sourcePath = null, mimeType = "text/plain")
        database.documentDao().insertDocument(doc)

        val fakeAiService = object : AiService {
            override suspend fun generateAssociation(text: String): String {
                return "Linked concept: $text"
            }

            override suspend fun generateRecallPrompt(text: String): RecallPromptData {
                return RecallPromptData("Question about: $text", "Answer for: $text")
            }

            override suspend fun generateAnswer(text: String): String {
                return "Answer: $text"
            }
        }

        // Generate two paragraphs that each exceed 200 words
        val p1Words = List(205) { "wordA$it" }.joinToString(" ")
        val p2Words = List(205) { "wordB$it" }.joinToString(" ")
        val fullText = "$p1Words\n\n$p2Words"

        val workerData = workDataOf(
            TextProcessingWorker.KEY_DOCUMENT_ID to docId,
            TextProcessingWorker.KEY_DOCUMENT_TEXT to fullText
        )

        val worker = TestListenableWorkerBuilder<TextProcessingWorker>(context)
            .setInputData(workerData)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters
                ): ListenableWorker {
                    return TextProcessingWorker(
                        appContext,
                        workerParameters,
                        customAiService = fakeAiService,
                        customDatabase = database
                    )
                }
            })
            .build()

        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)

        val chunks = database.memoryChunkDao().getChunksForDocument(docId)
        assertEquals(2, chunks.size)

        val promptsForChunk1 = database.recallPromptDao().getPromptsForChunk(chunks[0].id)
        assertEquals(1, promptsForChunk1.size)
        assertTrue(promptsForChunk1[0].question.contains("Question about:"))

        val associations = database.associationDao().getAssociationsForChunk(chunks[1].id)
        assertEquals(1, associations.size)
        assertEquals(chunks[0].id, associations[0].sourceChunkId)
        assertEquals(chunks[1].id, associations[0].targetChunkId)
    }
}
