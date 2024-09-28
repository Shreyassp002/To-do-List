package com.rey.todo.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rey.todo.common.ScreenViewState
import com.rey.todo.data.local.model.Todo
import java.util.Date

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
    onDeleteTodo: (Long) -> Unit,
    onTodoClicked: (Long) -> Unit,
) {
    when (state.todos) {
        is ScreenViewState.Loading -> {
            CircularProgressIndicator()
        }

        is ScreenViewState.Success -> {
            // Mutable state for todos to allow reordering
            var todos by remember { mutableStateOf(state.todos.data) }

            HomeDetail(
                todos = todos,
                modifier = modifier,
                onDeleteTodo = onDeleteTodo,
                onTodoClicked = onTodoClicked,
                onMove = { from, to ->
                    val newList = todos.toMutableList()
                    val movedItem = newList.removeAt(from)
                    newList.add(to, movedItem)
                    todos = newList
                },
                onDragFinished = {
                    // Perform any action after drag finishes, e.g., save to DB
                }
            )
        }

        is ScreenViewState.Error -> {
            Text(
                text = state.todos.message ?: "Unknown Error",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun HomeDetail(
    todos: List<Todo>,
    modifier: Modifier,
    onDeleteTodo: (Long) -> Unit,
    onTodoClicked: (Long) -> Unit,
    onMove: (Int, Int) -> Unit,
    onDragFinished: () -> Unit,
) {
    DragDropList(
        items = todos,
        onMove = onMove,
        onDragFinished = onDragFinished
    ) { todo ->
        TodoCard(
            todo = todo,
            onDeleteTodo = onDeleteTodo,
            onTodoClicked = onTodoClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoCard(
    todo: Todo,
    onDeleteTodo: (Long) -> Unit,
    onTodoClicked: (Long) -> Unit,
) {
    val shape = RoundedCornerShape(50f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = shape,
        onClick = { onTodoClicked(todo.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = todo.title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = todo.content,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.clickable { onDeleteTodo(todo.id) }
                )
            }
        }
    }
}

@Composable
fun <T> DragDropList(
    items: List<T>,
    onMove: (Int, Int) -> Unit,
    onDragFinished: () -> Unit,
    itemContent: @Composable (T) -> Unit
) {
    // State for tracking dragged item
    var draggingItemIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffsetY by remember { mutableStateOf(0f) }

    LazyColumn {
        itemsIndexed(items) { index, item ->
            // Each item has a modifier for detecting dragging gestures
            val modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            draggingItemIndex = null
                            onDragFinished() // Final action after dragging ends
                        },
                        onDragCancel = {
                            draggingItemIndex = null
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            draggingOffsetY += dragAmount.y

                            // Move item when dragging over a threshold
                            draggingItemIndex?.let { fromIndex ->
                                val targetIndex = (fromIndex + draggingOffsetY / 100).toInt()
                                    .coerceIn(0, items.size - 1)
                                if (targetIndex != fromIndex) {
                                    onMove(fromIndex, targetIndex)
                                    draggingItemIndex = targetIndex
                                    draggingOffsetY = 0f
                                }
                            }
                        },
                        onDragStart = {
                            draggingItemIndex = index
                        }
                    )
                }

            Box(
                modifier = if (index == draggingItemIndex) {
                    modifier.graphicsLayer {
                        translationY = draggingOffsetY // Apply Y offset while dragging
                    }
                } else {
                    modifier
                }
            ) {
                itemContent(item)
            }
        }
    }
}



@Preview(showSystemUi = true)
@Composable
fun PrevHome() {
    HomeScreen(
        state = HomeState(
            todos = ScreenViewState.Success(todos)
        ),
        onDeleteTodo = {},
        onTodoClicked = {}
    )
}

val placeHolderText =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas porttitor nunc vel metus mollis suscipit. Phasellus nec eros id ex aliquam scelerisque. Phasellus quis feugiat eros. Nam sodales ante ac lorem convallis tempus. Sed lacinia consequat diam at ultrices. Nullam lacinia dignissim aliquam. Proin sit amet quam efficitur, euismod nunc eu, aliquam orci. Ut mattis orci a purus ultricies sodales. Pellentesque odio quam, aliquet nec accumsan et, pharetra et lacus. Pellentesque faucibus, dolor quis iaculis fringilla, ligula nisl imperdiet massa, vel volutpat velit elit ac magna. Interdum et malesuada fames ac ante ipsum primis in faucibus. Vivamus pharetra dolor nec magna condimentum volutpat. "

val todos = listOf(
    Todo(
        title = "Room Database",
        content = placeHolderText + placeHolderText,
        createdDate = Date()
    ),
    Todo(
        title = "JetPack Compose",
        content = "Testing",
        createdDate = Date(),

        ),
    Todo(
        title = "Room Database",
        content = placeHolderText + placeHolderText,
        createdDate = Date()
    ),
    Todo(
        title = "JetPack Compose",
        content = placeHolderText,
        createdDate = Date()
        ),
    Todo(
        title = "Room Database",
        content = placeHolderText,
        createdDate = Date()
    ),
    Todo(
        title = "JetPack Compose",
        content = placeHolderText + placeHolderText,
        createdDate = Date()
    ),
)