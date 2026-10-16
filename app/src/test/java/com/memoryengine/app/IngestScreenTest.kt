package com.memoryengine.app

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.ui.ingest.IngestScreen
import com.memoryengine.app.ui.ingest.IngestUiState
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
class IngestScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureIngestScreenEmptyState() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                IngestScreen(
                    uiState = IngestUiState.Idle
                )
            }
        }

        val outputFile = File("../docs/screenshots/ingest_screen_empty.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }

    @Test
    fun captureIngestScreenLoadingState() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                IngestScreen(
                    uiState = IngestUiState.Loading(
                        message = "Processing document & extracting memory chunks..."
                    )
                )
            }
        }

        val outputFile = File("../docs/screenshots/ingest_screen_loading.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
