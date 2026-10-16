package com.memoryengine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.ui.components.AnimatedProgressRing
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
class AnimatedProgressRingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureAnimatedProgressRing() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    AnimatedProgressRing(
                        progress = 0.85f,
                        title = "Retention Rate",
                        helpTitle = "Memory Retention Rate",
                        helpConcept = "Calculates predicted memory retention across stored memory chunks.",
                        helpTakeaway = "Maintain retention above 80% by completing daily recall prompts."
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/animated_progress_ring.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
