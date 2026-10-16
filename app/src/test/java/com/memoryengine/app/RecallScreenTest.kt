package com.memoryengine.app

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.ui.screens.RecallScreen
import com.memoryengine.app.ui.screens.RecallUiState
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
class RecallScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureRecallScreen_QuestionState() {
        val state = RecallUiState(
            currentCardIndex = 3,
            totalCards = 10,
            question = "What is the primary function of the hippocampus in memory consolidation?",
            answer = "The hippocampus plays a crucial role in transferring short-term memory to long-term memory.",
            association = "Visualize a giant hippo wearing a graduation cap navigating a memory maze.",
            isAnswerRevealed = false
        )

        composeTestRule.setContent {
            MemoryEngineTheme {
                RecallScreen(
                    uiState = state,
                    onRevealAnswer = {},
                    onRatingSelected = {}
                )
            }
        }

        val outputFile = File("../docs/screenshots/recall_screen_question.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureRecallScreen_AnswerRevealedState() {
        val state = RecallUiState(
            currentCardIndex = 3,
            totalCards = 10,
            question = "What is the primary function of the hippocampus in memory consolidation?",
            answer = "The hippocampus plays a crucial role in transferring short-term memory to long-term memory.",
            association = "Visualize a giant hippo wearing a graduation cap navigating a memory maze.",
            isAnswerRevealed = true
        )

        composeTestRule.setContent {
            MemoryEngineTheme {
                RecallScreen(
                    uiState = state,
                    onRevealAnswer = {},
                    onRatingSelected = {}
                )
            }
        }

        val outputFile = File("../docs/screenshots/recall_screen_answer_revealed.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
