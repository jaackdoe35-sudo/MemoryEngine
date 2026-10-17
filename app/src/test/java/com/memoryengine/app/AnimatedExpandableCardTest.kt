package com.memoryengine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.ui.components.AnimatedExpandableCard
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
class AnimatedExpandableCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun captureAnimatedExpandableCard() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.padding(16.dp)) {
                    AnimatedExpandableCard(
                        title = "Neural Plasticity & Synaptic Pruning",
                        subtitle = "4 prompts due for active review",
                        badgeText = "Memory Chunk #102",
                        initialExpanded = true,
                        helpTitle = "Memory Consolidation",
                        helpConcept = "Active retrieval triggers synaptic plasticity.",
                        helpTakeaway = "Engage in active recall before inspecting hints."
                    ) {
                        Text(
                            text = "Expanded details showing prompt questions and recall intervals.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        val outputFile = File("../docs/screenshots/animated_expandable_card.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot())
            .captureRoboImage(outputFile)
    }
}
