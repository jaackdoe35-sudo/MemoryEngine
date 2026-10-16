package com.memoryengine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.ui.components.ContextualHelpBottomSheetContent
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
class ContextualHelpBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureContextualHelpBottomSheet() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    ContextualHelpBottomSheetContent(
                        title = "Spaced Repetition Engine",
                        concept = "Active recall strengthens neural pathways through optimal review intervals.",
                        takeaway = "Review items when prompted to maintain maximum retention with minimal effort.",
                        onDismissRequest = {}
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/contextual_help_bottom_sheet.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
