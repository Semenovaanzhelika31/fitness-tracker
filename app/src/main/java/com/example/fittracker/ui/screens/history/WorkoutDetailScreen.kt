package com.example.fittracker.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittracker.data.model.WorkoutExerciseWithExercise
import com.example.fittracker.data.model.WorkoutSet
import com.example.fittracker.ui.components.ConfirmDeleteDialog
import com.example.fittracker.ui.components.EditDeleteButtons
import com.example.fittracker.ui.components.EmptyState
import com.example.fittracker.ui.components.NumberField
import com.example.fittracker.ui.util.formatDuration
import com.example.fittracker.ui.util.formatKg
import com.example.fittracker.ui.util.toDateString
import com.example.fittracker.ui.util.toTimeString

private data class SetFormTarget(val workoutExerciseId: Long, val set: WorkoutSet?)

@Composable
fun WorkoutDetailScreen(
    onBack: () -> Unit,
    viewModel: WorkoutDetailViewModel = viewModel()
) {
    val workout by viewModel.workout.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()
    val sets by viewModel.sets.collectAsStateWithLifecycle()
    val exercises by viewModel.exercises.collectAsStateWithLifecycle()

    var showPicker by remember { mutableStateOf(false) }
    var orderTarget by remember { mutableStateOf<WorkoutExerciseWithExercise?>(null) }
    var deleteItem by remember { mutableStateOf<WorkoutExerciseWithExercise?>(null) }
    var setForm by remember { mutableStateOf<SetFormTarget?>(null) }
    var deleteSet by remember { mutableStateOf<WorkoutSet?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text("Тренировка", style = MaterialTheme.typography.headlineSmall)
        }

        val current = workout
        if (current == null) {
            EmptyState("Тренировка не найдена.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(current.startTime.toDateString(), style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${current.startTime.toTimeString()}–${current.endTime.toTimeString()} · " +
                                    formatDuration(current.durationSeconds)
                            )
                            if (current.notes.isNotBlank()) Text(current.notes, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                item {
                    Button(onClick = { showPicker = true }) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Text("Добавить упражнение", modifier = Modifier.padding(start = 8.dp))
                    }
                }
                items(items, key = { it.workoutExercise.id }) { item ->
                    val itemSets = sets.filter { it.workoutExerciseId == item.workoutExercise.id }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "${item.workoutExercise.orderIndex}. ${item.exercise.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                EditDeleteButtons(
                                    onEdit = { orderTarget = item },
                                    onDelete = { deleteItem = item }
                                )
                            }
                            itemSets.forEach { set ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        describeSet(set),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    EditDeleteButtons(
                                        onEdit = { setForm = SetFormTarget(item.workoutExercise.id, set) },
                                        onDelete = { deleteSet = set }
                                    )
                                }
                            }
                            TextButton(
                                onClick = { setForm = SetFormTarget(item.workoutExercise.id, null) }
                            ) { Text("+ Добавить подход") }
                        }
                    }
                }
            }
        }
    }

    // ---- выбор упражнения ----
    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("Выберите упражнение") },
            text = {
                if (exercises.isEmpty()) {
                    Text("В каталоге пока нет упражнений. Добавьте их на вкладке «Упражнения».")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                        items(exercises, key = { it.id }) { exercise ->
                            TextButton(
                                onClick = { viewModel.addExercise(exercise); showPicker = false },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text(exercise.name, modifier = Modifier.fillMaxWidth()) }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Отмена") } }
        )
    }

    // ---- изменение порядкового номера ----
    orderTarget?.let { target ->
        var text by remember(target) { mutableStateOf(target.workoutExercise.orderIndex.toString()) }
        AlertDialog(
            onDismissRequest = { orderTarget = null },
            title = { Text("Порядковый номер") },
            text = { NumberField("Номер в тренировке", text, { text = it }) },
            confirmButton = {
                TextButton(
                    enabled = text.toIntOrNull() != null,
                    onClick = {
                        viewModel.updateOrder(target.workoutExercise, text.toInt())
                        orderTarget = null
                    }
                ) { Text("Сохранить") }
            },
            dismissButton = { TextButton(onClick = { orderTarget = null }) { Text("Отмена") } }
        )
    }

    deleteItem?.let { target ->
        ConfirmDeleteDialog(
            message = "Убрать «${target.exercise.name}» из тренировки вместе с подходами?",
            onConfirm = { viewModel.deleteExercise(target.workoutExercise); deleteItem = null },
            onDismiss = { deleteItem = null }
        )
    }

    setForm?.let { form ->
        val nextNumber = sets.count { it.workoutExerciseId == form.workoutExerciseId } + 1
        SetFormDialog(
            initial = form.set,
            defaultNumber = nextNumber,
            onDismiss = { setForm = null },
            onSave = { number, reps, weight, duration, distance ->
                viewModel.saveSet(
                    WorkoutSet(
                        id = form.set?.id ?: 0,
                        workoutExerciseId = form.workoutExerciseId,
                        setNumber = number,
                        reps = reps,
                        weightKg = weight,
                        durationSeconds = duration,
                        distanceMeters = distance
                    )
                )
                setForm = null
            }
        )
    }

    deleteSet?.let { set ->
        ConfirmDeleteDialog(
            message = "Удалить подход ${set.setNumber}?",
            onConfirm = { viewModel.deleteSet(set); deleteSet = null },
            onDismiss = { deleteSet = null }
        )
    }
}

private fun describeSet(set: WorkoutSet): String = buildList {
    add("Подход ${set.setNumber}")
    if (set.reps > 0) add("${set.reps} повт.")
    if (set.weightKg > 0) add("${formatKg(set.weightKg)} кг")
    if (set.durationSeconds > 0) add("${set.durationSeconds} с")
    if (set.distanceMeters > 0) add("${set.distanceMeters} м")
}.joinToString(" · ")

@Composable
private fun SetFormDialog(
    initial: WorkoutSet?,
    defaultNumber: Int,
    onDismiss: () -> Unit,
    onSave: (number: Int, reps: Int, weight: Double, duration: Int, distance: Int) -> Unit
) {
    var number by remember { mutableStateOf((initial?.setNumber ?: defaultNumber).toString()) }
    var reps by remember { mutableStateOf(initial?.reps?.toString() ?: "") }
    var weight by remember { mutableStateOf(initial?.weightKg?.let { if (it > 0) formatKg(it) else "" } ?: "") }
    var duration by remember { mutableStateOf(initial?.durationSeconds?.toString() ?: "") }
    var distance by remember { mutableStateOf(initial?.distanceMeters?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Новый подход" else "Изменить подход") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberField("Номер подхода", number, { number = it })
                NumberField("Повторения", reps, { reps = it })
                NumberField("Вес, кг", weight, { weight = it }, decimal = true)
                NumberField("Длительность, с (кардио)", duration, { duration = it })
                NumberField("Расстояние, м (кардио)", distance, { distance = it })
            }
        },
        confirmButton = {
            TextButton(
                enabled = number.toIntOrNull() != null,
                onClick = {
                    onSave(
                        number.toInt(),
                        reps.toIntOrNull() ?: 0,
                        weight.replace(',', '.').toDoubleOrNull() ?: 0.0,
                        duration.toIntOrNull() ?: 0,
                        distance.toIntOrNull() ?: 0
                    )
                }
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
