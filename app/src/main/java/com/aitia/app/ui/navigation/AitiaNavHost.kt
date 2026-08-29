package com.aitia.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aitia.app.di.AppContainer
import com.aitia.app.di.ViewModelFactory
import com.aitia.app.domain.model.IssueType
import com.aitia.app.ui.analytics.AnalyticsScreen
import com.aitia.app.ui.analytics.AnalyticsViewModel
import com.aitia.app.ui.components.AitiaBottomNavigation
import com.aitia.app.ui.detail.IssueDetailScreen
import com.aitia.app.ui.detail.IssueDetailViewModel
import com.aitia.app.ui.home.HomeScreen
import com.aitia.app.ui.home.HomeViewModel
import com.aitia.app.ui.issues.IssuesScreen
import com.aitia.app.ui.issues.IssuesViewModel
import com.aitia.app.ui.onboarding.OnboardingScreen
import com.aitia.app.ui.projects.ProjectDetailScreen
import com.aitia.app.ui.projects.ProjectsScreen
import com.aitia.app.ui.projects.ProjectsViewModel
import com.aitia.app.ui.quickcapture.QuickCaptureBottomSheet
import com.aitia.app.ui.quickcapture.QuickCaptureViewModel
import com.aitia.app.ui.sessions.TestingSessionSummaryScreen
import com.aitia.app.ui.settings.SettingsScreen
import com.aitia.app.ui.settings.SettingsViewModel

@Composable
fun AitiaNavHost(
    appContainer: AppContainer,
    hasCompletedOnboarding: Boolean,
    onCompleteOnboarding: () -> Unit,
    initialTriggerQuickCapture: Boolean = false,
    navController: NavHostController = rememberNavController()
) {
    val factory = remember { ViewModelFactory(appContainer) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    var isQuickCaptureOpen by remember { mutableStateOf(initialTriggerQuickCapture) }
    
    androidx.compose.runtime.LaunchedEffect(initialTriggerQuickCapture) {
        if (initialTriggerQuickCapture) {
            isQuickCaptureOpen = true
        }
    }
    val quickCaptureViewModel: QuickCaptureViewModel = viewModel(factory = factory)

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Issues.route,
        Screen.Projects.route,
        Screen.Analytics.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar && hasCompletedOnboarding) {
                AitiaBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigateToRoute = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onQuickCaptureClick = { isQuickCaptureOpen = true }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (hasCompletedOnboarding) Screen.Home.route else Screen.Onboarding.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 1. Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        onCompleteOnboarding()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Home Screen
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(factory = factory)
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToIssueDetail = { id ->
                        navController.navigate(Screen.IssueDetail.createRoute(id))
                    },
                    onNavigateToIssues = {
                        navController.navigate(Screen.Issues.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onQuickCaptureWithType = { type ->
                        quickCaptureViewModel.onTypeSelected(type)
                        isQuickCaptureOpen = true
                    }
                )
            }

            // 3. Issues Screen
            composable(Screen.Issues.route) {
                val issuesViewModel: IssuesViewModel = viewModel(factory = factory)
                IssuesScreen(
                    viewModel = issuesViewModel,
                    onNavigateToIssueDetail = { id ->
                        navController.navigate(Screen.IssueDetail.createRoute(id))
                    },
                    onQuickCapture = { isQuickCaptureOpen = true }
                )
            }

            // 4. Projects Screen
            composable(Screen.Projects.route) {
                val projectsViewModel: ProjectsViewModel = viewModel(factory = factory)
                ProjectsScreen(
                    viewModel = projectsViewModel,
                    onNavigateToProjectDetail = { id ->
                        navController.navigate(Screen.ProjectDetail.createRoute(id))
                    },
                    onNavigateToSessionSummary = { id ->
                        navController.navigate(Screen.SessionSummary.createRoute(id))
                    }
                )
            }

            // 5. Project Detail Screen
            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
                val projectsViewModel: ProjectsViewModel = viewModel(factory = factory)
                val projectsUiState by projectsViewModel.uiState.collectAsState()
                val issuesViewModel: IssuesViewModel = viewModel(factory = factory)
                val issuesUiState by issuesViewModel.uiState.collectAsState()

                val project = projectsUiState.projects.firstOrNull { it.id == projectId }
                val projectIssues = issuesUiState.issues.filter { it.projectId == projectId }

                ProjectDetailScreen(
                    projectId = projectId,
                    viewModel = projectsViewModel,
                    project = project,
                    projectIssues = projectIssues,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToIssueDetail = { id ->
                        navController.navigate(Screen.IssueDetail.createRoute(id))
                    }
                )
            }

            // 6. Analytics Screen
            composable(Screen.Analytics.route) {
                val analyticsViewModel: AnalyticsViewModel = viewModel(factory = factory)
                AnalyticsScreen(
                    viewModel = analyticsViewModel,
                    onNavigateToIssueDetail = { id ->
                        navController.navigate(Screen.IssueDetail.createRoute(id))
                    }
                )
            }

            // 7. Settings Screen
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 8. Issue Detail Screen
            composable(
                route = Screen.IssueDetail.route,
                arguments = listOf(navArgument("issueId") { type = NavType.LongType })
            ) { backStackEntry ->
                val issueId = backStackEntry.arguments?.getLong("issueId") ?: 0L
                val detailViewModel: IssueDetailViewModel = viewModel(factory = factory)
                IssueDetailScreen(
                    issueId = issueId,
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 9. Testing Session Summary Screen
            composable(
                route = Screen.SessionSummary.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                val projectsViewModel: ProjectsViewModel = viewModel(factory = factory)
                val projectsUiState by projectsViewModel.uiState.collectAsState()
                val issuesViewModel: IssuesViewModel = viewModel(factory = factory)
                val issuesUiState by issuesViewModel.uiState.collectAsState()

                val session = projectsUiState.sessions.firstOrNull { it.id == sessionId }
                val sessionIssues = issuesUiState.issues.filter { it.testingSessionId == sessionId }

                TestingSessionSummaryScreen(
                    session = session,
                    sessionIssues = sessionIssues,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToIssueDetail = { id ->
                        navController.navigate(Screen.IssueDetail.createRoute(id))
                    }
                )
            }
        }

        // Global Quick Capture Bottom Sheet Modal
        if (isQuickCaptureOpen) {
            QuickCaptureBottomSheet(
                viewModel = quickCaptureViewModel,
                onDismiss = { isQuickCaptureOpen = false },
                onIssueCreated = { newId ->
                    isQuickCaptureOpen = false
                    navController.navigate(Screen.IssueDetail.createRoute(newId))
                },
                onOpenExistingIssue = { existing ->
                    isQuickCaptureOpen = false
                    navController.navigate(Screen.IssueDetail.createRoute(existing.id))
                }
            )
        }
    }
}
