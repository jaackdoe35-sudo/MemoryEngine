package com.memoryengine.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.memoryengine.app.domain.scheduler.Rating
import com.memoryengine.app.ui.components.AppNavigationBar
import com.memoryengine.app.ui.components.NavigationTab
import com.memoryengine.app.ui.library.LibraryScreen
import com.memoryengine.app.ui.library.LibraryUiState
import com.memoryengine.app.ui.palace.MemoryPalaceUiState
import com.memoryengine.app.ui.palace.PalaceScreen
import com.memoryengine.app.ui.screens.RecallScreen
import com.memoryengine.app.ui.screens.RecallUiState
import com.memoryengine.app.ui.theme.MemoryEngineTheme
import com.memoryengine.app.ui.today.TodayScreen
import com.memoryengine.app.ui.today.TodayUiState

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
    initialTodayState: TodayUiState = TodayUiState(
        duePromptsCount = 5,
        totalPromptsCount = 18,
        completedTodayCount = 2,
        unfinishedDocumentsCount = 1,
        focusScore = 88,
        streakDays = 7
    )
) {
    var currentTab by remember { mutableStateOf(initialTab) }
    var recallState by remember {
        mutableStateOf(
            RecallUiState(
                currentCardIndex = 1,
                totalCards = 5,
                question = "What is active recall?",
                answer = "A learning technique where the mind actively stimulates memory retrieval.",
                association = "Picture a glowing lightbulb flashing in your brain as you open a memory vault.",
                isAnswerRevealed = false
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AppNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.TODAY -> TodayScreen(
                    state = initialTodayState,
                    onStartReview = { currentTab = NavigationTab.RECALL },
                    onContinueIngest = { currentTab = NavigationTab.LIBRARY },
                    onAddFact = { currentTab = NavigationTab.LIBRARY }
                )
                NavigationTab.PALACE -> PalaceScreen(
                    state = MemoryPalaceUiState(),
                    onSelectChunk = {},
                    onCreateAssociation = {}
                )
                NavigationTab.RECALL -> RecallScreen(
                    uiState = recallState,
                    onRevealAnswer = {
                        recallState = recallState.copy(isAnswerRevealed = true)
                    },
                    onRatingSelected = { rating ->
                        if (recallState.currentCardIndex < recallState.totalCards) {
                            recallState = recallState.copy(
                                currentCardIndex = recallState.currentCardIndex + 1,
                                isAnswerRevealed = false
                            )
                        } else {
                            currentTab = NavigationTab.TODAY
                        }
                    },
                    onBackToToday = { currentTab = NavigationTab.TODAY }
                )
                NavigationTab.LIBRARY -> LibraryScreen(
                    uiState = LibraryUiState(),
                    onSelectDocument = {},
                    onBackFromDrilldown = {},
                    onToggleHelpSheet = {},
                    windowWidthSizeClass = windowSizeClass?.widthSizeClass
                )
            }
        }
    }
}
