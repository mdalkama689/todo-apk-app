package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.TaskFilter

@Composable
fun EmptyState(
  filter: TaskFilter,
  searchQuery: String,
  onAddTask: () -> Unit,
  onResetSamples: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val (icon, title, subtitle) = when {
    searchQuery.isNotBlank() -> Triple(
      Icons.Default.SearchOff,
      "No matching tasks",
      "We couldn't find any tasks matching \"$searchQuery\"."
    )
    filter == TaskFilter.COMPLETED -> Triple(
      Icons.Default.DoneAll,
      "No completed tasks yet",
      "Check off items from your to-do list to see them here."
    )
    filter == TaskFilter.TODAY -> Triple(
      Icons.Default.CheckCircleOutline,
      "No tasks due today",
      "You have no pending deadlines today. Enjoy your day or plan ahead!"
    )
    filter == TaskFilter.UPCOMING -> Triple(
      Icons.Default.PlaylistAddCheck,
      "No upcoming tasks",
      "You're all clear for future deadlines."
    )
    else -> Triple(
      Icons.Default.PlaylistAddCheck,
      "No tasks here yet",
      "Stay organized and get things done by adding your first task."
    )
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 32.dp, vertical = 48.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Stylized Icon Halo
    Box(
      modifier = Modifier
        .size(96.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(36.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Primary action
    if (searchQuery.isBlank()) {
      Button(
        onClick = onAddTask,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.testTag("empty_add_task_button")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text("Create Task")
      }
    }

    if (onResetSamples != null && searchQuery.isBlank() && filter == TaskFilter.ALL) {
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedButton(
        onClick = onResetSamples,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.testTag("load_samples_button")
      ) {
        Text("Load Sample Tasks")
      }
    }
  }
}
