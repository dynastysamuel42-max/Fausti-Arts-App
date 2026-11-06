package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.FaustiViewModel
import com.example.ui.components.FaustiBottomBar
import com.example.ui.components.FaustiNavRail
import com.example.ui.components.FaustiTopBar
import com.example.ui.components.NavDestination
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PortfolioScreen
import com.example.ui.screens.ProjectDetailScreen
import com.example.ui.screens.ProjectRequestScreen
import com.example.ui.screens.ProjectsListScreen
import com.example.ui.screens.ServicesScreen
import com.example.ui.screens.SettingsScreen

enum class AppScreen {
    HOME,
    SERVICES,
    PROJECTS,
    PORTFOLIO,
    MESSAGES,
    PROJECT_REQUEST,
    PROJECT_DETAIL,
    ADMIN_DASHBOARD,
    NOTIFICATIONS,
    SETTINGS,
    AUTH
}

@Composable
fun AppNavigation(
    viewModel: FaustiViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedServiceCategoryForRequest by remember { mutableStateOf("Logo Design and Branding") }

    val unreadNotifsCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    val isAdminOrManager = currentUser?.role == "ADMIN" || currentUser?.role == "MANAGER"

    // Back handler management
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        currentScreen = when (currentScreen) {
            AppScreen.PROJECT_DETAIL -> AppScreen.PROJECTS
            AppScreen.PROJECT_REQUEST -> AppScreen.SERVICES
            AppScreen.ADMIN_DASHBOARD -> AppScreen.HOME
            AppScreen.NOTIFICATIONS -> AppScreen.HOME
            AppScreen.SETTINGS -> AppScreen.HOME
            AppScreen.AUTH -> AppScreen.SETTINGS
            else -> AppScreen.HOME
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 600.dp

        val currentNavDestination = when (currentScreen) {
            AppScreen.HOME -> NavDestination.HOME
            AppScreen.SERVICES, AppScreen.PROJECT_REQUEST -> NavDestination.SERVICES
            AppScreen.PROJECTS, AppScreen.PROJECT_DETAIL -> NavDestination.PROJECTS
            AppScreen.MESSAGES -> NavDestination.MESSAGES
            AppScreen.SETTINGS, AppScreen.AUTH, AppScreen.ADMIN_DASHBOARD, AppScreen.NOTIFICATIONS -> NavDestination.PROFILE
            AppScreen.PORTFOLIO -> NavDestination.HOME
        }

        Row(modifier = Modifier.fillMaxSize()) {
            if (isExpandedScreen) {
                FaustiNavRail(
                    currentDestination = currentNavDestination,
                    onNavigate = { dest ->
                        currentScreen = when (dest) {
                            NavDestination.HOME -> AppScreen.HOME
                            NavDestination.SERVICES -> AppScreen.SERVICES
                            NavDestination.PROJECTS -> AppScreen.PROJECTS
                            NavDestination.MESSAGES -> AppScreen.PROJECTS // opens projects where chat tabs reside
                            NavDestination.PROFILE -> AppScreen.SETTINGS
                        }
                    },
                    isAdminOrManager = isAdminOrManager,
                    onAdminClick = { currentScreen = AppScreen.ADMIN_DASHBOARD }
                )
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    if (currentScreen != AppScreen.AUTH && currentScreen != AppScreen.PROJECT_DETAIL && currentScreen != AppScreen.NOTIFICATIONS) {
                        FaustiTopBar(
                            currentUser = currentUser,
                            unreadNotificationsCount = unreadNotifsCount,
                            onNotificationsClick = { currentScreen = AppScreen.NOTIFICATIONS },
                            onProfileClick = {
                                if (currentUser == null) currentScreen = AppScreen.AUTH
                                else currentScreen = AppScreen.SETTINGS
                            }
                        )
                    }
                },
                bottomBar = {
                    if (!isExpandedScreen && currentScreen != AppScreen.AUTH && currentScreen != AppScreen.PROJECT_DETAIL) {
                        FaustiBottomBar(
                            currentDestination = currentNavDestination,
                            onNavigate = { dest ->
                                currentScreen = when (dest) {
                                    NavDestination.HOME -> AppScreen.HOME
                                    NavDestination.SERVICES -> AppScreen.SERVICES
                                    NavDestination.PROJECTS -> AppScreen.PROJECTS
                                    NavDestination.MESSAGES -> AppScreen.PROJECTS
                                    NavDestination.PROFILE -> AppScreen.SETTINGS
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        AppScreen.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onExploreServices = { currentScreen = AppScreen.SERVICES },
                            onRequestProject = {
                                selectedServiceCategoryForRequest = "Logo Design and Branding"
                                currentScreen = AppScreen.PROJECT_REQUEST
                            },
                            onViewPortfolio = { currentScreen = AppScreen.PORTFOLIO },
                            onViewProjects = { currentScreen = AppScreen.PROJECTS }
                        )

                        AppScreen.SERVICES -> ServicesScreen(
                            viewModel = viewModel,
                            onRequestService = { category ->
                                selectedServiceCategoryForRequest = category
                                currentScreen = AppScreen.PROJECT_REQUEST
                            }
                        )

                        AppScreen.PROJECT_REQUEST -> ProjectRequestScreen(
                            viewModel = viewModel,
                            initialCategory = selectedServiceCategoryForRequest,
                            onProjectCreated = { newProjId ->
                                viewModel.selectProject(newProjId)
                                currentScreen = AppScreen.PROJECT_DETAIL
                            }
                        )

                        AppScreen.PROJECTS, AppScreen.MESSAGES -> ProjectsListScreen(
                            viewModel = viewModel,
                            onSelectProject = { projId ->
                                viewModel.selectProject(projId)
                                currentScreen = AppScreen.PROJECT_DETAIL
                            },
                            onRequestNewProject = {
                                selectedServiceCategoryForRequest = "Custom Creative Projects"
                                currentScreen = AppScreen.PROJECT_REQUEST
                            }
                        )

                        AppScreen.PROJECT_DETAIL -> ProjectDetailScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = AppScreen.PROJECTS }
                        )

                        AppScreen.PORTFOLIO -> PortfolioScreen(
                            viewModel = viewModel
                        )

                        AppScreen.ADMIN_DASHBOARD -> AdminDashboardScreen(
                            viewModel = viewModel,
                            onSelectProject = { projId ->
                                viewModel.selectProject(projId)
                                currentScreen = AppScreen.PROJECT_DETAIL
                            }
                        )

                        AppScreen.NOTIFICATIONS -> NotificationsScreen(
                            viewModel = viewModel,
                            onSelectProject = { projId ->
                                viewModel.selectProject(projId)
                                currentScreen = AppScreen.PROJECT_DETAIL
                            },
                            onBack = { currentScreen = AppScreen.HOME }
                        )

                        AppScreen.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = onToggleDarkTheme,
                            onOpenAuth = { currentScreen = AppScreen.AUTH }
                        )

                        AppScreen.AUTH -> AuthScreen(
                            viewModel = viewModel,
                            onAuthSuccess = { currentScreen = AppScreen.HOME }
                        )
                    }
                }
            }
        }
    }
}
