package com.rey.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.rey.todo.presentation.detail.DetailAssistedFactory
import com.rey.todo.presentation.home.HomeViewModel
import com.rey.todo.presentation.navigation.Screens
import com.rey.todo.presentation.navigation.TodoNavigation
import com.rey.todo.presentation.navigation.navigateToSingleTop
import com.rey.todo.ui.theme.ToDoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var assistedFactory: DetailAssistedFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToDoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) {
                    TodoApp(assistedFactory = assistedFactory)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(assistedFactory: DetailAssistedFactory) {
    val homeViewModel: HomeViewModel = viewModel()
    val navController = rememberNavController()
    var currentTab by remember { mutableStateOf(TabScreen.Home) }
    Scaffold(
        floatingActionButton = @Composable {
            FloatingActionButton(onClick = {
                navController.navigateToSingleTop(route = "${Screens.Detail}")
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },

    ) {
        TodoNavigation(
            modifier = Modifier.padding(it),
            navHostController = navController,
            homeViewModel = homeViewModel,
            assistedFactory = assistedFactory
        )
    }

}


enum class TabScreen {
    Home
}



