package com.memoryengine.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.memoryengine.app.data.local.database.AppDatabase
import com.memoryengine.app.data.repository.DocumentRepository
import com.memoryengine.app.data.repository.MemoryChunkRepository
import com.memoryengine.app.data.repository.RecallPromptRepository
import com.memoryengine.app.ui.components.AppNavigationBar
import com.memoryengine.app.ui.components.NavigationItem
import com.memoryengine.app.ui.ingest.IngestScreen
import com.memoryengine.app.ui.library.LibraryScreen
import com.memoryengine.app.ui.library.LibraryViewModel
import com.memoryengine.app.ui.theme.MemoryEngineTheme
import com.memoryengine.app.ui.today.TodayScreen
import com.memoryengine.app.ui.today.TodayUiState
import com.memoryengine.app.ui.today.TodayViewModel

enum class NavigationTab(val id: String, val label: String) {
    INGEST("ingest", "Ingest"),
    TODAY("today", "Today"),
    LIBRARY("library", "Library")
}

val NavigationItems = listOf(
    NavigationItem("ingest", "Ingest", Icons.Outlined.PostAdd),
    NavigationItem("today", "Today", Icons.Outlined.Today),
    NavigationItem("library", "Library", Icons.Outlined.FolderOpen)
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MemoryEngineTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppScreen(windowSizeClass = windowSizeClass)
                }
            }
        }
    }
}

@Composable
fun MainAppScreen(
    windowSizeClass: WindowSizeClass? = null,
    initialTab: NavigationTab = NavigationTab.TODAY,
    initialState: TodayUiState? = null
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val documentRepo = remember { DocumentRepository(database.documentDao()) }
    val promptRepo = remember { RecallPromptRepository(database.recallPromptDao()) }
    val chunkRepo = remember { MemoryChunkRepository(database.memoryChunkDao()) }

    val todayViewModel = remember { TodayViewModel(documentRepo, promptRepo) }
    val libraryViewModel = remember { LibraryViewModel(documentRepo, chunkRepo, promptRepo) }

    val todayState by todayViewModel.uiState.collectAsState()
    val libraryState by libraryViewModel.uiState.collectAsState()

    var currentTabId by remember { mutableStateOf(initialTab.id) }

    val activeTodayState = initialState ?: todayState

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppNavigationBar(
                items = NavigationItems,
                selectedItemId = currentTabId,
                onItemSelected = { currentTabId = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTabId) {
                NavigationTab.INGEST.id -> IngestScreen()
                NavigationTab.TODAY.id -> TodayScreen(
                    state = activeTodayState,
                    onStartReview = { currentTabId = NavigationTab.LIBRARY.id },
                    onContinueIngest = { currentTabId = NavigationTab.INGEST.id },
                    onAddFact = { currentTabId = NavigationTab.INGEST.id }
                )
                NavigationTab.LIBRARY.id -> LibraryScreen(
                    uiState = libraryState,
                    onSelectDocument = { libraryViewModel.selectDocument(it) },
                    onBackFromDrilldown = { libraryViewModel.clearSelection() },
                    onToggleHelpSheet = { libraryViewModel.toggleHelpSheet(it) },
                    windowWidthSizeClass = windowSizeClass?.widthSizeClass
                )
            }
        }
    }
}
