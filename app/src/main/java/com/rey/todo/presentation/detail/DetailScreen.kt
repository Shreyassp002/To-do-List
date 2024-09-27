@file:Suppress("UNUSED_EXPRESSION")

package com.rey.todo.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DetailScreen(
    modifier: Modifier = Modifier,
    todoId: Long,
    assistedFactory: DetailAssistedFactory,
    navigateUp: () -> Unit,
) {
    val viewModel = viewModel(
        modelClass = DetailViewModel::class.java,
        factory = DetailedViewModelFactory(
            todoId = todoId,
            assistedFactory = assistedFactory
        )
    )

    val state = viewModel.state
    DetailScreen(
        modifier = modifier,
        isUpdatingTodo = state.isUpdatingTodo,
        title = state.title,
        content = state.content,
        isFormNotBlank = viewModel.isFormNotBlank,
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        onBtnClick = {
            viewModel.addOrUpdateTodo()
            navigateUp()
        },
        onNavigate = navigateUp
    )
}

@Composable
private fun DetailScreen(
    modifier: Modifier,
    isUpdatingTodo: Boolean,
    title: String,
    content: String,
    isFormNotBlank: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onBtnClick: () -> Unit,
    onNavigate: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TopSection(
            title = title,
            onTitleChange = onTitleChange,
            onNavigate = onNavigate,
            isFormNotBlank = isFormNotBlank,
            onBtnClick = onBtnClick,
            isUpdatingTodo = isUpdatingTodo
        )
        Spacer(modifier = Modifier.size(2.dp))
        TodosTextField(
            modifier = Modifier.weight(1f),
            value = content,
            label = "Content",
            onValueChange = onContentChange,
            fontSize = 16,
        )

    }
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    onNavigate: () -> Unit,
    isFormNotBlank: Boolean,
    onBtnClick: () -> Unit,
    isUpdatingTodo: Boolean,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = null,
            modifier = Modifier.clickable { onNavigate }
        )

        TodosTextField(
            modifier = Modifier.weight(1f),
            value = title,
            label = "Title",
            labelAlign = TextAlign.Center,
            onValueChange = onTitleChange,
            fontSize = 24
        )

        AnimatedVisibility (isFormNotBlank) {
            IconButton(onClick = onBtnClick) {
                val icon = if (isUpdatingTodo) Icons.Default.Upload
                else Icons.Default.Check
                Icon(imageVector = icon, contentDescription = null)
            }
        }
    }
}

@Composable
private fun TodosTextField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    fontSize: Int,
    labelAlign: TextAlign? = null,
) {
    OutlinedTextField(
        value = value,

        onValueChange = onValueChange,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            disabledContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(
                text = "Insert $label",
                textAlign = labelAlign,
                modifier = modifier,
                fontSize = fontSize.sp

            )
        }
    )
}