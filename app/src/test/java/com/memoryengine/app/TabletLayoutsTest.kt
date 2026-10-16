package com.memoryengine.app

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
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
import com.memoryengine.app.ui.today.TodayScreen
import com.memoryengine.app.ui.today.TodayUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w1280dp-h800dp")
class TabletLayoutsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureMainAppScreenTablet() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                MainAppScreen()
            }
        }

        val outputFile = File("../docs/screenshots/main_app_screen_tablet.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureLibraryScreenTabletTwoPane() {
        val sampleDoc = Document(
            id = "doc-tablet-1",
            title = "Quantum Computing Principles",
            sourcePath = null,
            mimeType = "application/pdf",
            chunkCount = 4
        )
        val uiItem = DocumentUiItem(
            document = sampleDoc,
            chunkCount = 4,
            promptCount = 12,
            progress = 0.80f
        )
        val sampleChunk = MemoryChunk(
            id = "chunk-1",
            documentId = sampleDoc.id,
            content = "Qubits exist in superposition states, allowing simultaneous computation of linear combinations.",
            orderIndex = 0,
            wordCount = 13
        )
        val samplePrompt = RecallPrompt(
            id = "prompt-1",
            chunkId = sampleChunk.id,
            question = "How does qubit superposition differ from classical bits?",
            answer = "Qubits can represent 0 and 1 simultaneously through linear quantum combinations."
        )

        composeTestRule.setContent {
            MemoryEngineTheme {
                LibraryScreen(
                    uiState = LibraryUiState(
                        documents = listOf(uiItem),
                        selectedDocument = uiItem,
                        selectedDocumentChunks = listOf(
                            ChunkWithPrompts(chunk = sampleChunk, prompts = listOf(samplePrompt))
                        )
                    ),
                    onSelectDocument = {},
                    onBackFromDrilldown = {},
                    onToggleHelpSheet = {}
                )
            }
        }

        val outputFile = File("../docs/screenshots/library_screen_tablet.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureTodayScreenTablet() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                TodayScreen(
                    state = TodayUiState(
                        duePromptsCount = 15,
                        totalPromptsCount = 42,
                        completedTodayCount = 5,
                        unfinishedDocumentsCount = 2,
                        focusScore = 91,
                        streakDays = 14
                    )
                )
            }
        }

        val outputFile = File("../docs/screenshots/today_screen_tablet.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
