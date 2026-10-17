package com.memoryengine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.data.local.entity.Document
import com.memoryengine.app.data.local.entity.MemoryChunk
import com.memoryengine.app.data.local.entity.RecallPrompt
import com.memoryengine.app.ui.library.ChunkWithPrompts
import com.memoryengine.app.ui.library.DocumentUiItem
import com.memoryengine.app.ui.library.LibraryScreen
import com.memoryengine.app.ui.library.LibraryUiState
import com.memoryengine.app.ui.theme.MemoryEngineTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class LibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureLibraryScreenDocumentList() {
        val sampleDocuments = listOf(
            DocumentUiItem(
                document = Document(
                    id = "doc-1",
                    title = "Memory Engine Architecture",
                    sourcePath = null,
                    mimeType = "text/plain",
                    chunkCount = 4
                ),
                chunkCount = 4,
                promptCount = 8,
                progress = 0.75f
            ),
            DocumentUiItem(
                document = Document(
                    id = "doc-2",
                    title = "Neuroscience of Spaced Repetition",
                    sourcePath = null,
                    mimeType = "text/plain",
                    chunkCount = 3
                ),
                chunkCount = 3,
                promptCount = 6,
                progress = 0.33f
            )
        )

        val state = LibraryUiState(
            documents = sampleDocuments,
            isLoading = false
        )

        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    LibraryScreen(
                        uiState = state,
                        onSelectDocument = {},
                        onBackFromDrilldown = {},
                        onToggleHelpSheet = {}
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/library_screen_list.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureLibraryScreenEmptyState() {
        val state = LibraryUiState(
            documents = emptyList(),
            isLoading = false
        )

        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    LibraryScreen(
                        uiState = state,
                        onSelectDocument = {},
                        onBackFromDrilldown = {},
                        onToggleHelpSheet = {}
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/library_screen_empty.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureLibraryScreenDrilldown() {
        val docItem = DocumentUiItem(
            document = Document(
                id = "doc-1",
                title = "Memory Engine Architecture",
                sourcePath = null,
                mimeType = "text/plain",
                chunkCount = 2
            ),
            chunkCount = 2,
            promptCount = 3,
            progress = 0.66f
        )

        val chunks = listOf(
            ChunkWithPrompts(
                chunk = MemoryChunk(
                    id = "chunk-1",
                    documentId = "doc-1",
                    content = "The core engine leverages SM2 spaced repetition algorithms to schedule reviews dynamically.",
                    orderIndex = 0,
                    wordCount = 12
                ),
                prompts = listOf(
                    RecallPrompt(
                        id = "prompt-1",
                        chunkId = "chunk-1",
                        question = "What scheduling algorithm does the core engine use?",
                        answer = "SM2 spaced repetition algorithm."
                    ),
                    RecallPrompt(
                        id = "prompt-2",
                        chunkId = "chunk-1",
                        question = "How are review intervals scheduled?",
                        answer = "Dynamically based on performance feedback."
                    )
                )
            ),
            ChunkWithPrompts(
                chunk = MemoryChunk(
                    id = "chunk-2",
                    documentId = "doc-1",
                    content = "Room database entities store documents, memory chunks, recall prompts, and visual associations.",
                    orderIndex = 1,
                    wordCount = 12
                ),
                prompts = listOf(
                    RecallPrompt(
                        id = "prompt-3",
                        chunkId = "chunk-2",
                        question = "What local database technology is used?",
                        answer = "Room database entities."
                    )
                )
            )
        )

        val state = LibraryUiState(
            selectedDocument = docItem,
            selectedDocumentChunks = chunks,
            isLoading = false
        )

        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    LibraryScreen(
                        uiState = state,
                        onSelectDocument = {},
                        onBackFromDrilldown = {},
                        onToggleHelpSheet = {}
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/library_screen_drilldown.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
