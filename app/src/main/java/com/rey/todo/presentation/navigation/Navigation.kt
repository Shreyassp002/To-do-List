package com.rey.todo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rey.todo.presentation.detail.DetailAssistedFactory
import com.rey.todo.presentation.detail.DetailScreen
import com.rey.todo.presentation.home.HomeScreen
import com.rey.todo.presentation.home.HomeViewModel

enum class Screens {
    Home, Detail
}

@Composable
fun TodoNavigation(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    homeViewModel: HomeViewModel,
    assistedFactory: DetailAssistedFactory,
) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.Home.name
    ) {
        composable(route = Screens.Home.name) {
            val state by homeViewModel.state.collectAsState()
            HomeScreen(
                state = state,
                onDeleteTodo = homeViewModel::deleteTodo,
                onTodoClicked = {
                    navHostController.navigateToSingleTop(
                        route = "${Screens.Detail.name}?id=$it"
                    )
                }
            )
        }
        composable(
            route = "${Screens.Detail.name}?id={id}",
            arguments = listOf(
                navArgument("id") {
                    NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            DetailScreen(
                todoId = id,
                assistedFactory = assistedFactory,
                navigateUp = { navHostController.navigateUp() }
            )
        }
    }

}

fun NavHostController.navigateToSingleTop(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}