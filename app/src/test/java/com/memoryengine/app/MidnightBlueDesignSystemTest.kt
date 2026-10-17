package com.memoryengine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.data.local.entity.MemoryChunk
import com.memoryengine.app.ui.components.AppCard
import com.memoryengine.app.ui.components.EmptyState
import com.memoryengine.app.ui.components.PrimaryButton
import com.memoryengine.app.ui.components.StatusChip
import com.memoryengine.app.ui.palace.MemoryPalaceUiState
import com.memoryengine.app.ui.palace.PalaceScreen
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
class MidnightBlueDesignSystemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureTodayScreenLight() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = false) {
                TodayScreen(
                    state = TodayUiState(
                        duePromptsCount = 4,
                        totalPromptsCount = 12,
                        completedTodayCount = 3,
                        unfinishedDocumentsCount = 1,
                        focusScore = 88,
                        streakDays = 7
                    )
                )
            }
        }
        val outputFile = File("docs/screenshots/today_screen_light.png")
        outputFile.parentFile?.mkdirs()
        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    fun captureTodayScreenDark() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                TodayScreen(
                    state = TodayUiState(
                        duePromptsCount = 4,
                        totalPromptsCount = 12,
                        completedTodayCount = 3,
                        unfinishedDocumentsCount = 1,
                        focusScore = 88,
                        streakDays = 7
                    )
                )
            }
        }
        val outputFile = File("docs/screenshots/today_screen_dark.png")
        outputFile.parentFile?.mkdirs()
        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    fun capturePalaceScreenLight() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = false) {
                PalaceScreen(
                    state = MemoryPalaceUiState(
                        palaceName = "Biological Energetics Palace",
                        roomName = "Mitochondria Hall",
                        chunks = listOf(
                            MemoryChunk(
                                id = "1",
                                documentId = "doc1",
                                content = "Glycolysis breaks down glucose into two pyruvate molecules in the cytoplasm.",
                                orderIndex = 0,
                                wordCount = 12
                            )
                        ),
                        selectedChunkId = "1"
                    )
                )
            }
        }
        val outputFile = File("docs/screenshots/palace_screen_light.png")
        outputFile.parentFile?.mkdirs()
        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    fun capturePalaceScreenDark() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                PalaceScreen(
                    state = MemoryPalaceUiState(
                        palaceName = "Biological Energetics Palace",
                        roomName = "Mitochondria Hall",
                        chunks = listOf(
                            MemoryChunk(
                                id = "1",
                                documentId = "doc1",
                                content = "Glycolysis breaks down glucose into two pyruvate molecules in the cytoplasm.",
                                orderIndex = 0,
                                wordCount = 12
                            )
                        ),
                        selectedChunkId = "1"
                    )
                )
            }
        }
        val outputFile = File("docs/screenshots/palace_screen_dark.png")
        outputFile.parentFile?.mkdirs()
        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    fun captureRecallScreenLight() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = false) {
                RecallScreen(
                    uiState = RecallUiState(
                        currentCardIndex = 1,
                        totalCards = 5,
                        question = "What primary product is generated at the end of Glycolysis?",
                        answer = "2 Pyruvate molecules, 2 net ATP, and 2 NADH.",
                        association = "Imagine two glowing flaming pyruvates bursting out of a sugar apple.",
                        isAnswerRevealed = true
                    ),
                    onRevealAnswer = {},
                    onRatingSelected = {}
                )
            }
        }
        val outputFile = File("docs/screenshots/recall_screen_light.png")
        outputFile.parentFile?.mkdirs()
        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    fun captureRecallScreenDark() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                RecallScreen(
                    uiState = RecallUiState(
                        currentCardIndex = 1,
                        totalCards = 5,
                        question = "What primary product is generated at the end of Glycolysis?",
                        answer = "2 Pyruvate molecules, 2 net ATP, and 2 NADH.",
                        association = "Imagine two glowing flaming pyruvates bursting out of a sugar apple.",
                        isAnswerRevealed = true
                    ),
                    onRevealAnswer = {},
                    onRatingSelected = {}
                )
            }
        }
        val outputFile = File("docs/screenshots/recall_screen_dark.png")
        outputFile.parentFile?.mkdirs()
        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    fun captureSharedComponentsLight() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = false) {
                Box(modifier = Modifier.padding(16.dp)) {
                    AppCard {
                        PrimaryButton(text = "Primary Action", onClick = {})
                        StatusChip(text = "ACTIVE STATUS", symbol = "✓")
                    }
                }
            }
        }
        val outputFile = File("docs/screenshots/shared_components_light.png")
        outputFile.parentFile?.mkdirs()
        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }

    @Test
    fun captureSharedComponentsDark() {
        composeTestRule.setContent {
            MemoryEngineTheme(darkTheme = true) {
                Box(modifier = Modifier.padding(16.dp)) {
                    AppCard {
                        PrimaryButton(text = "Primary Action", onClick = {})
                        StatusChip(text = "ACTIVE STATUS", symbol = "✓")
                    }
                }
            }
        }
        val outputFile = File("docs/screenshots/shared_components_dark.png")
        outputFile.parentFile?.mkdirs()
        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
    }
}
