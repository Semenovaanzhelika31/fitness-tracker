package com.example.fittracker.ui.screens.history

import androidx.compose.foundation.clickable
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
import com.example.fittracker.data.model.Workout
import com.example.fittracker.ui.components.ConfirmDeleteDialog
import com.example.fittracker.ui.components.EditDeleteButtons
import com.example.fittracker.ui.components.EmptyState
import com.example.fittracker.ui.components.ScreenTitle
import com.example.fittracker.ui.util.formatDuration
import com.example.fittracker.ui.util.parseDateTime
import com.example.fittracker.ui.util.parseDay
import com.example.fittracker.ui.util.toDateString
import com.example.fittracker.ui.util.toTimeString

@Composable
fun HistoryScreen(
    onOpenWorkout: (Long) -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    val hasUser by viewModel.hasUser.collectAsStateWithLifecycle()
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Workout?>(null) }
    var deleting by remember { mutableStateOf<Workout?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            if (hasUser) {
                FloatingActionButton(onClick = { editing = null; showForm = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Добавить тренировку")
                }
            }
        }
    ) { padding ->
        when {
            !hasUser -> EmptyState(
                text = "Выберите или создайте пользователя на вкладке «Профиль», чтобы вести историю тренировок.",
                modifier = Modifier.padding(padding)
            )
            workouts.isEmpty() -> EmptyState(
                text = "Тренировок пока нет. Нажмите «+», чтобы добавить первую.",
                modifier = Modifier.padding(padding)
            )
            else -> LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { ScreenTitle(stringResource(R.string.history_title)) }
                items(workouts, key = { it.id }) { workout ->
                    WorkoutCard(
                        workout = workout,
                        onOpen = { onOpenWorkout(workout.id) },
                        onEdit = { editing = workout; showForm = true },
                        onDelete = { deleting = workout }
                    )
                }
            }
        }
    }

    if (showForm) {
        WorkoutFormDialog(
            initial = editing,
            onDismiss = { showForm = false },
            onSave = { fields ->
                val old = editing
                viewModel.save(
                    Workout(
                        id = old?.id ?: 0,
                        userId = old?.userId ?: viewModel.userId,
                        date = fields.date,
                        startTime = fields.start,
                        endTime = fields.end,
                        durationSeconds = (fields.end - fields.start) / 1000,
                        notes = fields.notes
                    )
                )
                showForm = false
            }
        )
    }
    deleting?.let { workout ->
        ConfirmDeleteDialog(
            message = "Удалить тренировку от ${workout.startTime.toDateString()} вместе с упражнениями и подходами?",
            onConfirm = { viewModel.delete(workout); deleting = null },
            onDismiss = { deleting = null }
        )
    }
}

@Composable
private fun WorkoutCard(
    workout: Workout,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onOpen)) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(workout.startTime.toDateString(), style = MaterialTheme.typography.titleMedium)
                Text(
                    "${workout.startTime.toTimeString()}–${workout.endTime.toTimeString()} · " +
                        formatDuration(workout.durationSeconds),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (workout.notes.isNotBlank()) {
                    Text(workout.notes, style = MaterialTheme.typography.bodySmall)
                }
            }
            EditDeleteButtons(onEdit = onEdit, onDelete = onDelete)
        }
    }
}

private data class WorkoutFields(val date: Long, val start: Long, val end: Long, val notes: String)

@Composable
private fun WorkoutFormDialog(
    initial: Workout?,
    onDismiss: () -> Unit,
    onSave: (WorkoutFields) -> Unit
) {
    val now = remember { System.currentTimeMillis() }
    var date by remember { mutableStateOf((initial?.startTime ?: now).toDateString()) }
    var startTime by remember { mutableStateOf((initial?.startTime ?: now).toTimeString()) }
    var endTime by remember { mutableStateOf((initial?.endTime ?: (now + 60 * 60 * 1000)).toTimeString()) }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }

    val start = parseDateTime(date, startTime)
    val end = parseDateTime(date, endTime)
    val day = parseDay(date)
    val error = when {
        day == null || start == null -> "Неверная дата или время начала (дд.мм.гггг, чч:мм)"
        end == null -> "Неверное время окончания (чч:мм)"
        end < start -> "Окончание раньше начала"
        else -> null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Новая тренировка" else "Изменить тренировку") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = date, onValueChange = { date = it },
                    label = { Text("Дата (дд.мм.гггг)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = startTime, onValueChange = { startTime = it },
                    label = { Text("Начало (чч:мм)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endTime, onValueChange = { endTime = it },
                    label = { Text("Окончание (чч:мм)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Заметки") }, modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = error == null,
                onClick = {
                    if (day != null && start != null && end != null) {
                        onSave(WorkoutFields(day, start, end, notes.trim()))
                    }
                }
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
