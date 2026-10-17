package com.memoryengine.app

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.takahirom.roborazzi.captureRoboImage
import com.memoryengine.app.data.local.database.AppDatabase
import com.memoryengine.app.data.remote.service.AiService
import com.memoryengine.app.data.remote.service.RecallPromptData
import com.memoryengine.app.data.repository.AssociationRepository
import com.memoryengine.app.data.repository.DocumentRepository
import com.memoryengine.app.data.repository.MemoryChunkRepository
import com.memoryengine.app.data.repository.RecallPromptRepository
import com.memoryengine.app.ui.components.ContextualHelpBottomSheetContent
import com.memoryengine.app.ui.screens.MemoryCreationContent
import com.memoryengine.app.ui.screens.MemoryCreationUiState
import com.memoryengine.app.ui.screens.MemoryCreationViewModel
import com.memoryengine.app.ui.theme.MemoryEngineTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class MemoryCreationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var database: AppDatabase
    private lateinit var context: Context

    private lateinit var documentRepository: DocumentRepository
    private lateinit var memoryChunkRepository: MemoryChunkRepository
    private lateinit var recallPromptRepository: RecallPromptRepository
    private lateinit var associationRepository: AssociationRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor { it.run() }
            .setTransactionExecutor { it.run() }
            .build()

        documentRepository = DocumentRepository(database.documentDao())
        memoryChunkRepository = MemoryChunkRepository(database.memoryChunkDao())
        recallPromptRepository = RecallPromptRepository(database.recallPromptDao())
        associationRepository = AssociationRepository(database.associationDao())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun `viewModel saveMemory with empty fact sets error`() = runTest {
        val viewModel = MemoryCreationViewModel(
            documentRepository = documentRepository,
            memoryChunkRepository = memoryChunkRepository,
            recallPromptRepository = recallPromptRepository,
            associationRepository = associationRepository,
            aiService = null
        )

        viewModel.saveMemory()

        assertEquals("Fact field cannot be empty", viewModel.uiState.value.factError)
    }

    @Test
    fun `viewModel saveMemory with blank association calls AiService and persists entities`() = runTest {
        var aiAssociationCalled = false
        val fakeAiService = object : AiService {
            override suspend fun generateAssociation(text: String): String {
                aiAssociationCalled = true
                return "Generated AI Mnemonic for: $text"
            }

            override suspend fun generateRecallPrompt(text: String): RecallPromptData {
                return RecallPromptData("Generated Question for: $text", text)
            }

            override suspend fun generateAnswer(text: String): String {
                return text
            }
        }

        val viewModel = MemoryCreationViewModel(
            documentRepository = documentRepository,
            memoryChunkRepository = memoryChunkRepository,
            recallPromptRepository = recallPromptRepository,
            associationRepository = associationRepository,
            aiService = fakeAiService
        )

        val factInput = "Speed of light in vacuum is approximately 299,792,458 meters per second."
        viewModel.onFactChange(factInput)
        viewModel.saveMemory()
        testScheduler.advanceUntilIdle()

        assertTrue(aiAssociationCalled)
        assertTrue(viewModel.uiState.value.isSaved)
        assertEquals("Generated AI Mnemonic for: $factInput", viewModel.uiState.value.associationText)

        val chunks = database.memoryChunkDao().getAllChunks()
        assertTrue(chunks.isNotEmpty())
        assertEquals(factInput, chunks[0].content)

        val doc = database.documentDao().getDocumentById(chunks[0].documentId)
        assertNotNull(doc)

        val prompts = database.recallPromptDao().getPromptsForChunk(chunks[0].id)
        assertEquals(1, prompts.size)
        assertEquals("Generated Question for: $factInput", prompts[0].question)

        val associations = database.associationDao().getAssociationsForChunk(chunks[0].id)
        assertEquals(1, associations.size)
    }

    @Test
    fun captureMemoryCreationScreen_default() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    MemoryCreationContent(
                        uiState = MemoryCreationUiState(),
                        onFactChange = {},
                        onAssociationChange = {},
                        onQuestionChange = {},
                        onSaveClick = {},
                        onHelpClick = {},
                        onDismissHelpSheet = {}
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/memory_creation_screen.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
        assertTrue(outputFile.exists())
    }

    @Test
    fun captureMemoryCreationScreen_filled() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    MemoryCreationContent(
                        uiState = MemoryCreationUiState(
                            factText = "Neuroplasticity is the ability of the brain to form and reorganize synaptic connections.",
                            associationText = "Brain tree expanding branches in the library courtyard",
                            questionText = "What mechanism allows the neural pathways to reorganize after learning?"
                        ),
                        onFactChange = {},
                        onAssociationChange = {},
                        onQuestionChange = {},
                        onSaveClick = {},
                        onHelpClick = {},
                        onDismissHelpSheet = {}
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/memory_creation_screen_filled.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
        assertTrue(outputFile.exists())
    }

    @Test
    fun captureMemoryCreationScreen_helpSheet() {
        composeTestRule.setContent {
            MemoryEngineTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    MemoryCreationContent(
                        uiState = MemoryCreationUiState(),
                        onFactChange = {},
                        onAssociationChange = {},
                        onQuestionChange = {},
                        onSaveClick = {},
                        onHelpClick = {},
                        onDismissHelpSheet = {}
                    )

                    ContextualHelpBottomSheetContent(
                        title = "Manual Fact Creation",
                        concept = "Adding discrete facts directly bypasses document chunking for instant active recall scheduling.",
                        takeaway = "Provide a fact, an optional location or association cue, and a prompt question to convert knowledge into long-term memory.",
                        onDismissRequest = {}
                    )
                }
            }
        }

        val outputFile = File("../docs/screenshots/memory_creation_screen_help_sheet.png")
        outputFile.parentFile?.mkdirs()

        composeTestRule.onNode(isRoot()).captureRoboImage(outputFile)
        assertTrue(outputFile.exists())
    }
}
