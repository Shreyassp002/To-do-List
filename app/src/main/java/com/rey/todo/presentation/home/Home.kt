package com.rey.todo.presentation.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import com.rey.todo.common.ScreenViewState
import com.rey.todo.data.local.model.Todo
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
    onDeleteTodo: (Long) -> Unit,
    onTodoClicked: (Long) -> Unit,
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Column(modifier = modifier) {

        SearchBar(
            query = searchQuery,
            onQueryChanged = { newQuery ->
                searchQuery = newQuery
            }
        )

        when (state.todos) {
            is ScreenViewState.Loading -> {
                CircularProgressIndicator()
            }

            is ScreenViewState.Success -> {

                var todos by remember(state.todos.data) { mutableStateOf(state.todos.data) }


                val filteredTodos = todos.filter {
                    it.title.contains(searchQuery.text, ignoreCase = true)
                }

                HomeDetail(
                    todos = filteredTodos,
                    modifier = modifier,
                    onDeleteTodo = onDeleteTodo,
                    onTodoClicked = onTodoClicked,
                    onMove = { from, to ->

                        if (from in todos.indices && to in todos.indices) {
                            todos = todos.toMutableList().apply { move(from, to) }
                        }
                    },
                    onDragFinished = {}
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
}




@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChanged: (TextFieldValue) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape
            )
            .height(56.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.size(8.dp))


            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.surface
                ),
                decorationBox = { innerTextField ->
                    if (query.text.isEmpty()) {
                        Text(
                            text = "Search todos...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                        )
                    }
                    innerTextField()
                }
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
            .padding(6.dp),
        onClick = { onTodoClicked(todo.id) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = todo.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 23.sp),
                        modifier = Modifier.weight(1f)
                    )


                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Todo",
                        modifier = Modifier
                            .clickable { onDeleteTodo(todo.id) }
                            .padding(start = 8.dp)
                            .size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))


                Text(
                    text = todo.content,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}



@Composable
fun <T : Any> DragDropList(
    items: List<T>,
    onMove: (Int, Int) -> Unit,
    onDragFinished: () -> Unit,
    modifier: Modifier = Modifier,
    itemComposable: @Composable (item: T) -> Unit
) {
    val scope = rememberCoroutineScope()
    var overscrollJob by remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onMove = onMove)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consume()
                        dragDropListState.onDrag(offset)

                        if (overscrollJob?.isActive == true) return@detectDragGesturesAfterLongPress

                        val overscrollAmount = dragDropListState.checkForOverScroll()
                        if (overscrollAmount != 0f) {
                            overscrollJob = scope.launch {
                                dragDropListState.lazyListState.scrollBy(overscrollAmount)
                            }
                        }
                    },
                    onDragStart = { offset ->
                        dragDropListState.onDragStart(offset)
                    },
                    onDragEnd = {
                        onDragFinished()
                        dragDropListState.onDragInterrupted()
                    },
                    onDragCancel = {
                        dragDropListState.onDragInterrupted()
                    }
                )
            },
        state = dragDropListState.lazyListState
    ) {
        itemsIndexed(items, key = { _, item -> (item as Todo).id }) { index, item ->
            val currentIndex = rememberUpdatedState(index)

            val rotationAngle by animateFloatAsState(
                targetValue = if (currentIndex.value == dragDropListState.currentIndexOfDraggedItem) 10f else 0f,
                animationSpec = tween(durationMillis = 1000)
            )

            Column(
                modifier = Modifier
                    .composed {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            index == dragDropListState.currentIndexOfDraggedItem
                        }
                        val translationYaxis by animateFloatAsState(targetValue = offsetOrNull ?: 0f)

                        Modifier
                            .graphicsLayer {
                                translationY = translationYaxis
                                rotationZ = rotationAngle
                            }
                    }
                    .fillMaxWidth()
                    .zIndex(if (currentIndex.value == dragDropListState.currentIndexOfDraggedItem) 1f else 0f)
            ) {
                itemComposable(item)
            }
        }
    }
}


@Composable
fun rememberDragDropListState(
    lazyListState: LazyListState = rememberLazyListState(),
    onMove: (Int, Int) -> Unit
): DragDropListState {
    return remember { DragDropListState(lazyListState = lazyListState, onMove = onMove) }
}

class DragDropListState(
    val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggedDistance by mutableStateOf(0f)
    var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)

    private val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let { Pair(it.offset, it.offset + it.size) }

    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let { lazyListState.getVisibleItemInfoFor(absoluteIndex = it) }
            ?.let { item -> (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset }

    private val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getVisibleItemInfoFor(absoluteIndex = it)
        }

    var overscrollJob by mutableStateOf<Job?>(null)

    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
            ?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
            }
    }

    fun onDragInterrupted() {
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overscrollJob?.cancel()
    }

    fun onDrag(offset: Offset) {
        draggedDistance += offset.y

        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            currentElement?.let { hovered ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { item -> (item.offset + item.size) < startOffset || item.offset > endOffset || hovered.index == item.index }
                    .firstOrNull { item ->
                        val delta = startOffset - hovered.offset
                        when {
                            delta > 0 -> (endOffset > item.offset + item.size)
                            else -> (startOffset < item.offset)
                        }
                    }
                    ?.also { item ->
                        currentIndexOfDraggedItem?.let { current ->
                            if (current in 0 until lazyListState.layoutInfo.totalItemsCount &&
                                item.index in 0 until lazyListState.layoutInfo.totalItemsCount) {
                                onMove.invoke(current, item.index)
                                currentIndexOfDraggedItem = item.index
                            }
                        }
                    }
            }
        }
    }

    fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offset + it.size + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }
                draggedDistance < 0 -> (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }
                else -> null
            }
        } ?: 0f
    }
}



fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from != to) {
        val item = removeAt(from)
        add(to, item)
    }
}


fun LazyListState.getVisibleItemInfoFor(absoluteIndex: Int): LazyListItemInfo? {
    return layoutInfo.visibleItemsInfo.firstOrNull { it.index == absoluteIndex }
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