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
import com.memoryengine.app.ui.components.ErrorState
import com.memoryengine.app.ui.ingest.IngestScreen
import com.memoryengine.app.ui.ingest.IngestUiState
import com.memoryengine.app.ui.library.ChunkWithPrompts
import com.memoryengine.app.ui.library.DocumentUiItem
import com.memoryengine.app.ui.library.LibraryScreen
import com.memoryengine.app.ui.library.LibraryUiState
import com.memoryengine.app.ui.palace.PalaceScreen
import com.memoryengine.app.ui.screens.DashboardScreen
import com.memoryengine.app.ui.screens.RecallScreen
import com.memoryengine.app.ui.screens.RecallUiState
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
class DarkModeAndTabletTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @Config(sdk = [33], qualifiers = "night")
    fun captureDashboardDarkMode() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                DashboardScreen()
            }
        }

        val outputFile = File("../docs/screenshots/dashboard_dark_mode.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    @Config(sdk = [33], qualifiers = "night")
    fun captureTodayDarkMode() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                TodayScreen(
                    state = TodayUiState(
                        duePromptsCount = 8,
                        totalPromptsCount = 24,
                        completedTodayCount = 4,
                        unfinishedDocumentsCount = 1,
                        focusScore = 92,
                        streakDays = 12
                    )
                )
            }
        }

        val outputFile = File("../docs/screenshots/today_dark_mode.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    @Config(sdk = [33], qualifiers = "night")
    fun capturePalaceDarkMode() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                PalaceScreen()
            }
        }

        val outputFile = File("../docs/screenshots/palace_dark_mode.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    @Config(sdk = [33], qualifiers = "night")
    fun captureRecallDarkMode() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                RecallScreen(
                    uiState = RecallUiState(
                        currentCardIndex = 2,
                        totalCards = 10,
                        question = "What is synaptic plasticity?",
                        answer = "The ability of synapses to strengthen or weaken over time.",
                        association = "Imagine a flexible rubber band stretching between two brain cells.",
                        isAnswerRevealed = true
                    ),
                    onRevealAnswer = {},
                    onRatingSelected = {}
                )
            }
        }

        val outputFile = File("../docs/screenshots/recall_dark_mode.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    @Config(sdk = [33], qualifiers = "w800dp-h1280dp-land")
    fun captureLibraryTabletLayout() {
        val sampleDoc = Document(
            id = "doc_1",
            title = "Neural Networks & Memory Consolidation",
            sourcePath = "/docs/nn.pdf",
            mimeType = "application/pdf"
        )
        val sampleChunk = MemoryChunk(
            id = "chunk_10",
            documentId = "doc_1",
            orderIndex = 0,
            content = "Synaptic plasticity underpins long-term memory formation.",
            wordCount = 8
        )
        val samplePrompt = RecallPrompt(
            id = "prompt_100",
            chunkId = "chunk_10",
            question = "What underpins long-term memory?",
            answer = "Synaptic plasticity"
        )

        val uiState = LibraryUiState(
            documents = listOf(
                DocumentUiItem(document = sampleDoc, chunkCount = 5, promptCount = 12, progress = 0.75f)
            ),
            selectedDocument = DocumentUiItem(document = sampleDoc, chunkCount = 5, promptCount = 12, progress = 0.75f),
            selectedDocumentChunks = listOf(
                ChunkWithPrompts(chunk = sampleChunk, prompts = listOf(samplePrompt))
            )
        )

        composeTestRule.setContent {
            MemoryEngineTheme {
                LibraryScreen(
                    uiState = uiState,
                    onSelectDocument = {},
                    onBackFromDrilldown = {},
                    onToggleHelpSheet = {}
                )
            }
        }

        val outputFile = File("../docs/screenshots/library_tablet_layout.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    @Config(sdk = [33])
    fun captureErrorStateComponent() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    ErrorState(
                        title = "AI Model Unreachable",
                        message = "Failed to connect to Gemini API. Please check network connection and try again.",
                        onRetry = {}
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/error_state_component.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    @Config(sdk = [33])
    fun captureIngestErrorState() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                IngestScreen(
                    uiState = IngestUiState.Error("Gemini API call timed out after 30 seconds.")
                )
            }
        }

        val outputFile = File("../docs/screenshots/ingest_error_state.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }
}
