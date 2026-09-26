package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityMedium
import com.example.util.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskSheet(
  task: Task?,
  onDismiss: () -> Unit,
  onSave: (id: Long, title: String, description: String, priority: TaskPriority, category: TaskCategory, dueDateMillis: Long?) -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember(task) { mutableStateOf(task?.title ?: "") }
  var description by remember(task) { mutableStateOf(task?.description ?: "") }
  var priority by remember(task) { mutableStateOf(task?.priority ?: TaskPriority.MEDIUM) }
  var category by remember(task) { mutableStateOf(task?.category ?: TaskCategory.PERSONAL) }
  var dueDateMillis by remember(task) { mutableStateOf(task?.dueDateMillis) }
  var titleError by remember { mutableStateOf(false) }

  var showDatePicker by remember { mutableStateOf(false) }
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(task) {
    if (task == null) {
      focusRequester.requestFocus()
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = modifier
      .navigationBarsPadding()
      .imePadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 24.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (task == null) "New Task" else "Edit Task",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Title input
      OutlinedTextField(
        value = title,
        onValueChange = {
          title = it
          if (it.isNotBlank()) titleError = false
        },
        label = { Text("What needs to be done?") },
        placeholder = { Text("e.g. Schedule dentist appointment") },
        isError = titleError,
        supportingText = if (titleError) {
          { Text("Title cannot be empty") }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .focusRequester(focusRequester)
          .testTag("task_title_input")
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Notes / Description input
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Notes or details (optional)") },
        placeholder = { Text("Add any extra notes or checklist items...") },
        minLines = 2,
        maxLines = 4,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_description_input")
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Priority Selector
      Text(
        text = "Priority",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TaskPriority.entries.forEach { p ->
          val isSelected = priority == p
          val tintColor = when (p) {
            TaskPriority.HIGH -> PriorityHigh
            TaskPriority.MEDIUM -> PriorityMedium
            TaskPriority.LOW -> PriorityLow
          }
          FilterChip(
            selected = isSelected,
            onClick = { priority = p },
            label = { Text(p.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(16.dp)
              )
            },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("priority_chip_${p.name.lowercase()}")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Category Selector
      Text(
        text = "Category",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        TaskCategory.entries.forEach { cat ->
          val isSelected = category == cat
          FilterChip(
            selected = isSelected,
            onClick = { category = cat },
            label = { Text(cat.label, fontSize = 12.sp) },
            leadingIcon = {
              Icon(
                imageVector = getCategoryIcon(cat),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
            },
            shape = RoundedCornerShape(10.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Due Date Selector
      Text(
        text = "Due Date",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))

      val todayStart = remember { DateUtils.getTodayStart() }
      val tomorrowStart = remember {
        Calendar.getInstance().apply {
          add(Calendar.DAY_OF_YEAR, 1)
          set(Calendar.HOUR_OF_DAY, 0)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
        }.timeInMillis
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // No Date Chip
        FilterChip(
          selected = dueDateMillis == null,
          onClick = { dueDateMillis = null },
          label = { Text("No date") },
          shape = RoundedCornerShape(10.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("date_chip_none")
        )

        // Today Chip
        FilterChip(
          selected = dueDateMillis != null && DateUtils.isToday(dueDateMillis!!),
          onClick = { dueDateMillis = todayStart },
          label = { Text("Today") },
          shape = RoundedCornerShape(10.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("date_chip_today")
        )

        // Tomorrow Chip
        FilterChip(
          selected = dueDateMillis != null && DateUtils.isTomorrow(dueDateMillis!!),
          onClick = { dueDateMillis = tomorrowStart },
          label = { Text("Tomorrow") },
          shape = RoundedCornerShape(10.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("date_chip_tomorrow")
        )

        // Custom Pick Date Chip
        val isCustom = dueDateMillis != null && !DateUtils.isToday(dueDateMillis!!) && !DateUtils.isTomorrow(dueDateMillis!!)
        FilterChip(
          selected = isCustom,
          onClick = { showDatePicker = true },
          label = {
            Text(
              if (isCustom) DateUtils.formatDueDate(dueDateMillis!!) else "Pick Date..."
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
          },
          shape = RoundedCornerShape(10.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("date_chip_custom")
        )
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Bottom Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("cancel_task_button")
        ) {
          Text("Cancel")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = {
            if (title.isBlank()) {
              titleError = true
            } else {
              onSave(
                task?.id ?: 0L,
                title,
                description,
                priority,
                category,
                dueDateMillis
              )
            }
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier.testTag("save_task_button")
        ) {
          Text(
            text = if (task == null) "Create Task" else "Save Changes",
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }

  // Material 3 Date Picker Dialog
  if (showDatePicker) {
    val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = dueDateMillis ?: System.currentTimeMillis()
    )

    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            datePickerState.selectedDateMillis?.let { selected ->
              // Adjust for timezone offset
              val cal = Calendar.getInstance().apply {
                timeInMillis = selected
                // Set to start of that day
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 0)
              }
              dueDateMillis = cal.timeInMillis
            }
            showDatePicker = false
          },
          modifier = Modifier.testTag("date_picker_confirm")
        ) {
          Text("OK")
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showDatePicker = false },
          modifier = Modifier.testTag("date_picker_cancel")
        ) {
          Text("Cancel")
        }
      }
    ) {
      DatePicker(state = datePickerState)
    }
  }
}
