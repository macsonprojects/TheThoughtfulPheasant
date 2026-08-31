package com.example.thoughtfulpheasant.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thoughtfulpheasant.data.MoodCategory
import com.example.thoughtfulpheasant.data.defaultMoodCategories
import com.example.thoughtfulpheasant.ui.theme.*
import com.example.thoughtfulpheasant.R

@Composable
fun EditorScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = viewModel()
) {
    val categories by viewModel.moodCategories.collectAsStateWithLifecycle()

    EditorScreenContent(
        categories = categories,
        onNavigateBack = onNavigateBack,
        onDeleteCategory = { viewModel.deleteCategory(it) },
        onUpdatePhrases = { name, phrases -> viewModel.updatePhrases(name, phrases) },
        onRenameCategory = { oldName, newName -> viewModel.renameCategory(oldName, newName) },
        onAddCategory = { viewModel.addCategory(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreenContent(
    categories: List<MoodCategory>,
    onNavigateBack: () -> Unit,
    onDeleteCategory: (String) -> Unit,
    onUpdatePhrases: (String, List<String>) -> Unit,
    onRenameCategory: (String, String) -> Unit,
    onAddCategory: (String) -> Unit
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.EditorBackground,
        topBar = {
            TopAppBar(
                title = { Text("Mood Editor", color = Color.PagerText) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.EditorPanels,
                    navigationIconContentColor = Color.PagerText,
                    titleContentColor = Color.PagerText,
                    actionIconContentColor = Color.PagerText
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddCategoryDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Category"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    onDelete = { onDeleteCategory(category.name) },
                    onUpdatePhrases = { phrases -> onUpdatePhrases(category.name, phrases) },
                    onRename = { newName -> onRenameCategory(category.name, newName) }
                )
            }
        }
    }

    if (showAddCategoryDialog) {
        NameDialog(
            title = "Add Category",
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name ->
                onAddCategory(name)
                showAddCategoryDialog = false
            }
        )
    }
}

@Composable
fun CategoryItem(
    category: MoodCategory,
    onDelete: () -> Unit,
    onUpdatePhrases: (List<String>) -> Unit,
    onRename: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.EditorPanels
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = category.name.replaceFirst(": ", ":\n"),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.PagerText,
                    modifier = Modifier.weight(1f),
                    lineHeight = 24.sp
                )
                IconButton(onClick = { showRenameDialog = true }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Rename",
                        tint = Color.PagerText
                    )
                }
                IconButton(onClick = { showDeleteConfirmation = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.PagerText
                    )
                }
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        text = if (expanded) "Hide Phrases" else "Show Phrases",
                        color = Color.HyperlinkBlue
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                PhraseEditor(
                    phrases = category.phrases,
                    onPhrasesChanged = onUpdatePhrases
                )
            }
        }
    }

    if (showRenameDialog) {
        NameDialog(
            title = "Rename Category",
            initialValue = category.name,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                onRename(newName)
                showRenameDialog = false
            }
        )
    }
    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            title = stringResource(R.string.delete_category_confirmation_title),
            message = stringResource(
                R.string.delete_category_confirmation_message,
                category.name
            ),
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                onDelete()
                showDeleteConfirmation = false
            }
        )
    }
}

@Composable
fun PhraseEditor(
    phrases: List<String>,
    onPhrasesChanged: (List<String>) -> Unit
) {
    var newPhrase by remember { mutableStateOf("") }
    var phraseToDeleteIndex by remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        phrases.forEachIndexed { index, phrase ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = phrase,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.PagerText
                )
                IconButton(onClick = { phraseToDeleteIndex = index }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Phrase",
                        modifier = Modifier.size(20.dp),
                        tint = Color.PagerText
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newPhrase,
                onValueChange = { newPhrase = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Add new phrase...", color = Color.PagerText.copy(alpha = 0.6f)) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.PagerText,
                    unfocusedTextColor = Color.PagerText,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.PagerText
                )
            )
            IconButton(onClick = {
                if (newPhrase.isNotBlank()) {
                    onPhrasesChanged(phrases + newPhrase)
                    newPhrase = ""
                }
            }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Phrase",
                    tint = Color.PagerText
                )
            }
        }
    }

    if (phraseToDeleteIndex != null) {
        DeleteConfirmationDialog(
            title = stringResource(R.string.delete_phrase_confirmation_title),
            message = stringResource(R.string.delete_phrase_confirmation_message),
            onDismiss = { phraseToDeleteIndex = null },
            onConfirm = {
                val newList = phrases.toMutableList()
                newList.removeAt(phraseToDeleteIndex!!)
                onPhrasesChanged(newList)
                phraseToDeleteIndex = null
            }
        )
    }
}

@Composable
fun NameDialog(
    title: String,
    initialValue: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.EditorPanels,
        titleContentColor = Color.PagerText,
        textContentColor = Color.PagerText,
        title = { Text(title) },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.PagerText,
                    unfocusedTextColor = Color.PagerText,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.PagerText,
                    focusedIndicatorColor = Color.PagerText,
                    unfocusedIndicatorColor = Color.PagerText.copy(alpha = 0.5f)
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("Confirm", color = Color.PagerText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.PagerText)
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.EditorPanels,
        titleContentColor = Color.PagerText,
        textContentColor = Color.PagerText,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.delete_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button), color = Color.PagerText)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditorScreenPreview() {
    ThoughtfulPheasantTheme {
        EditorScreenContent(
            categories = defaultMoodCategories,
            onNavigateBack = {},
            onDeleteCategory = {},
            onUpdatePhrases = { _, _ -> },
            onRenameCategory = { _, _ -> },
            onAddCategory = {}
        )
    }
}
