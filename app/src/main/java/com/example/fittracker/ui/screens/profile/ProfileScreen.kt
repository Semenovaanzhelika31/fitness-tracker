package com.example.fittracker.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import com.example.fittracker.data.model.Goal
import com.example.fittracker.data.model.User
import com.example.fittracker.data.prefs.ThemeMode
import com.example.fittracker.ui.components.ConfirmDeleteDialog
import com.example.fittracker.ui.components.EditDeleteButtons
import com.example.fittracker.ui.components.OptionChips
import com.example.fittracker.ui.components.ScreenTitle
import com.example.fittracker.ui.components.Stepper

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<User?>(null) }
    var deleting by remember { mutableStateOf<User?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ScreenTitle(stringResource(R.string.profile_title)) }

        // ---- Пользователи (CRUD) ----
        item { Text("Пользователи", style = MaterialTheme.typography.titleMedium) }
        if (users.isEmpty()) {
            item { Text("Пользователей пока нет. Добавьте первого.", style = MaterialTheme.typography.bodyMedium) }
        }
        items(users, key = { it.id }) { user ->
            val isCurrent = user.id == settings.currentUserId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectUser(user) },
                colors = if (isCurrent) {
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                } else {
                    CardDefaults.cardColors()
                }
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Цель: ${user.goal.title}" + if (isCurrent) " · текущий" else "",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    EditDeleteButtons(
                        onEdit = { editing = user; showForm = true },
                        onDelete = { deleting = user }
                    )
                }
            }
        }
        item {
            Button(onClick = { editing = null; showForm = true }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Добавить пользователя", modifier = Modifier.padding(start = 8.dp))
            }
        }

        // ---- Настройки (SharedPreferences) ----
        item { Text("Настройки", style = MaterialTheme.typography.titleMedium) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Тема оформления", style = MaterialTheme.typography.labelLarge)
                OptionChips(
                    options = ThemeMode.entries.toList(),
                    selected = settings.themeMode,
                    label = { it.title },
                    onSelect = viewModel::setThemeMode
                )
            }
        }
        item {
            Stepper(
                label = "Отсчёт перед подходом",
                value = settings.countdownSeconds,
                range = 0..30,
                onChange = viewModel::setCountdownSeconds,
                valueText = { "$it с" }
            )
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Напоминания о тренировке", modifier = Modifier.weight(1f))
                Switch(
                    checked = settings.remindersEnabled,
                    onCheckedChange = viewModel::setRemindersEnabled
                )
            }
        }
        if (settings.remindersEnabled) {
            item {
                Stepper(
                    label = "Час",
                    value = settings.reminderHour,
                    range = 0..23,
                    onChange = { viewModel.setReminderTime(it, settings.reminderMinute) },
                    valueText = { "%02d".format(it) }
                )
            }
            item {
                Stepper(
                    label = "Минуты",
                    value = settings.reminderMinute,
                    range = 0..55,
                    step = 5,
                    onChange = { viewModel.setReminderTime(settings.reminderHour, it) },
                    valueText = { "%02d".format(it) }
                )
            }
        }
    }

    if (showForm) {
        UserFormDialog(
            initial = editing,
            onDismiss = { showForm = false },
            onSave = { viewModel.saveUser(it); showForm = false }
        )
    }
    deleting?.let { user ->
        ConfirmDeleteDialog(
            message = "Удалить пользователя «${user.name}» вместе со всеми его тренировками?",
            onConfirm = { viewModel.deleteUser(user); deleting = null },
            onDismiss = { deleting = null }
        )
    }
}

@Composable
private fun UserFormDialog(
    initial: User?,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var goal by remember { mutableStateOf(initial?.goal ?: Goal.MAINTENANCE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Новый пользователь" else "Изменить пользователя") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Имя") }, singleLine = true,
                    isError = name.isBlank(), modifier = Modifier.fillMaxWidth()
                )
                Text("Цель", style = MaterialTheme.typography.labelLarge)
                OptionChips(
                    options = Goal.entries.toList(),
                    selected = goal,
                    label = { it.title },
                    onSelect = { goal = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = { onSave(User(id = initial?.id ?: 0, name = name.trim(), goal = goal)) }
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
