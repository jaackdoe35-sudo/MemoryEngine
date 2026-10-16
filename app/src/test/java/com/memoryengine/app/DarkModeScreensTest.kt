package com.memoryengine.app

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.data.local.entity.Document
import com.memoryengine.app.ui.ingest.IngestScreen
import com.memoryengine.app.ui.ingest.IngestUiState
import com.memoryengine.app.ui.library.DocumentUiItem
import com.memoryengine.app.ui.library.LibraryScreen
import com.memoryengine.app.ui.library.LibraryUiState
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
@Config(sdk = [33])
class DarkModeScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureDashboardScreenDark() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                DashboardScreen()
            }
        }

        val outputFile = File("../docs/screenshots/dashboard_screen_dark.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureTodayScreenDark() {
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

        val outputFile = File("../docs/screenshots/today_screen_dark.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureLibraryScreenDark() {
        val sampleDoc = Document(
            id = "doc-1",
            title = "Neural Networks Architecture",
            sourcePath = null,
            mimeType = "application/pdf",
            chunkCount = 6
        )
        val uiItem = DocumentUiItem(
            document = sampleDoc,
            chunkCount = 6,
            promptCount = 18,
            progress = 0.75f
        )

        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                LibraryScreen(
                    uiState = LibraryUiState(documents = listOf(uiItem)),
                    onSelectDocument = {},
                    onBackFromDrilldown = {},
                    onToggleHelpSheet = {}
                )
            }
        }

        val outputFile = File("../docs/screenshots/library_screen_dark.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureRecallScreenDark() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                RecallScreen(
                    uiState = RecallUiState(
                        currentCardIndex = 3,
                        totalCards = 10,
                        question = "What is the primary mechanism of backpropagation?",
                        answer = "Calculating the gradient of the loss function with respect to each weight by applying the chain rule.",
                        association = "Imagine tracing a blue electric current backwards through a branching tree.",
                        isAnswerRevealed = true
                    ),
                    onRevealAnswer = {},
                    onRatingSelected = {}
                )
            }
        }

        val outputFile = File("../docs/screenshots/recall_screen_dark.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureIngestScreenErrorDark() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                IngestScreen(
                    uiState = IngestUiState.Error(
                        message = "AI Service temporarily unreachable. Check network connection and retry.",
                        onRetry = {}
                    )
                )
            }
        }

        val outputFile = File("../docs/screenshots/ingest_screen_error_dark.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
