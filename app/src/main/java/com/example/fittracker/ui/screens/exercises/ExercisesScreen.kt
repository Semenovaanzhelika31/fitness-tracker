package com.example.fittracker.ui.screens.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittracker.R
import com.example.fittracker.data.model.Exercise
import com.example.fittracker.data.model.ExerciseCategory
import com.example.fittracker.ui.components.ConfirmDeleteDialog
import com.example.fittracker.ui.components.EditDeleteButtons
import com.example.fittracker.ui.components.EmptyState
import com.example.fittracker.ui.components.OptionChips
import com.example.fittracker.ui.components.ScreenTitle

@Composable
fun ExercisesScreen(viewModel: ExercisesViewModel = viewModel()) {
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Exercise?>(null) }
    var deleting by remember { mutableStateOf<Exercise?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showForm = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить упражнение")
            }
        }
    ) { padding ->
        if (exercises.isEmpty()) {
            EmptyState(
                text = "Упражнений пока нет. Нажмите «+», чтобы добавить первое.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { ScreenTitle(stringResource(R.string.exercises_title)) }
                items(exercises, key = { it.id }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onEdit = { editing = exercise; showForm = true },
                        onDelete = { deleting = exercise }
                    )
                }
            }
        }
    }

    if (showForm) {
        ExerciseFormDialog(
            initial = editing,
            onDismiss = { showForm = false },
            onSave = {
                viewModel.save(it)
                showForm = false
            }
        )
    }
    deleting?.let { exercise ->
        ConfirmDeleteDialog(
            message = "Удалить упражнение «${exercise.name}»? Оно пропадёт и из тренировок.",
            onConfirm = { viewModel.delete(exercise); deleting = null },
            onDismiss = { deleting = null }
        )
    }
}

@Composable
private fun ExerciseCard(exercise: Exercise, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                val details = listOf(
                    exercise.category.title,
                    exercise.muscleGroup,
                    "сложность ${exercise.difficulty}/5"
                ).filter { it.isNotBlank() }.joinToString(" · ")
                Text(details, style = MaterialTheme.typography.bodySmall)
                if (exercise.description.isNotBlank()) {
                    Text(
                        exercise.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            EditDeleteButtons(onEdit = onEdit, onDelete = onDelete)
        }
    }
}

@Composable
private fun ExerciseFormDialog(
    initial: Exercise?,
    onDismiss: () -> Unit,
    onSave: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: ExerciseCategory.STRENGTH) }
    var muscleGroup by remember { mutableStateOf(initial?.muscleGroup ?: "") }
    var difficulty by remember { mutableStateOf(initial?.difficulty ?: 1) }
    var animationUrl by remember { mutableStateOf(initial?.animationUrl ?: "") }
    var videoUrl by remember { mutableStateOf(initial?.videoUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Новое упражнение" else "Изменить упражнение") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Название") }, singleLine = true,
                    isError = name.isBlank(), modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Описание") }, modifier = Modifier.fillMaxWidth()
                )
                Text("Категория", style = MaterialTheme.typography.labelLarge)
                OptionChips(
                    options = ExerciseCategory.entries.toList(),
                    selected = category,
                    label = { it.title },
                    onSelect = { category = it }
                )
                OutlinedTextField(
                    value = muscleGroup, onValueChange = { muscleGroup = it },
                    label = { Text("Группа мышц") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Сложность", style = MaterialTheme.typography.labelLarge)
                OptionChips(
                    options = listOf(1, 2, 3, 4, 5),
                    selected = difficulty,
                    label = { it.toString() },
                    onSelect = { difficulty = it }
                )
                OutlinedTextField(
                    value = animationUrl, onValueChange = { animationUrl = it },
                    label = { Text("URL анимации техники") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = videoUrl, onValueChange = { videoUrl = it },
                    label = { Text("URL видео") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    onSave(
                        Exercise(
                            id = initial?.id ?: 0,
                            name = name.trim(),
                            description = description.trim(),
                            category = category,
                            muscleGroup = muscleGroup.trim(),
                            difficulty = difficulty,
                            animationUrl = animationUrl.trim(),
                            videoUrl = videoUrl.trim()
                        )
                    )
                }
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
