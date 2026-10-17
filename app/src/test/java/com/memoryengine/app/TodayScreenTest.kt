package com.memoryengine.app

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
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
class TodayScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureEmptyState() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                TodayScreen(
                    state = TodayUiState(
                        duePromptsCount = 0,
                        totalPromptsCount = 0,
                        completedTodayCount = 0,
                        unfinishedDocumentsCount = 0,
                        focusScore = 0,
                        streakDays = 0
                    )
                )
            }
        }

        val outputFile = File("../docs/screenshots/today_empty_state.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureReviewsDueState() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                TodayScreen(
                    state = TodayUiState(
                        duePromptsCount = 12,
                        totalPromptsCount = 35,
                        completedTodayCount = 3,
                        unfinishedDocumentsCount = 2,
                        focusScore = 88,
                        streakDays = 7
                    )
                )
            }
        }

        val outputFile = File("../docs/screenshots/today_reviews_due_state.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureAllCaughtUpState() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                TodayScreen(
                    state = TodayUiState(
                        duePromptsCount = 0,
                        totalPromptsCount = 35,
                        completedTodayCount = 15,
                        unfinishedDocumentsCount = 0,
                        focusScore = 95,
                        streakDays = 8
                    )
                )
            }
        }

        val outputFile = File("../docs/screenshots/today_all_caught_up_state.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
