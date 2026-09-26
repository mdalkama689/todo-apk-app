package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.data.model.TaskCategory
import com.example.data.model.TaskPriority
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityHighContainer
import com.example.ui.theme.PriorityLow
import com.example.ui.theme.PriorityLowContainer
import com.example.ui.theme.PriorityMedium
import com.example.ui.theme.PriorityMediumContainer
import com.example.util.DateUtils

@Composable
fun TaskItemCard(
  task: Task,
  onToggleComplete: () -> Unit,
  onClick: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val checkScale by animateFloatAsState(
    targetValue = if (task.isCompleted) 1.0f else 0.0f,
    label = "check_scale"
  )

  val cardAlpha by animateFloatAsState(
    targetValue = if (task.isCompleted) 0.65f else 1.0f,
    label = "card_alpha"
  )

  val checkboxBgColor by animateColorAsState(
    targetValue = if (task.isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent,
    label = "checkbox_bg"
  )

  val checkboxBorderColor by animateColorAsState(
    targetValue = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
    label = "checkbox_border"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .alpha(cardAlpha)
      .testTag("task_card_${task.id}")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (task.isCompleted)
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      else
        MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(
      defaultElevation = if (task.isCompleted) 0.dp else 1.5.dp
    ),
    border = BorderStroke(
      width = 1.dp,
      color = if (task.isCompleted)
        Color.Transparent
      else
        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      // Custom Checkbox
      Box(
        modifier = Modifier
          .size(48.dp)
          .testTag("task_check_${task.id}")
          .clickable(onClick = onToggleComplete),
        contentAlignment = Alignment.Center
      ) {
        Surface(
          modifier = Modifier.size(24.dp),
          shape = CircleShape,
          color = checkboxBgColor,
          border = BorderStroke(2.dp, checkboxBorderColor)
        ) {
          if (checkScale > 0.05f) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .scale(checkScale),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = if (task.isCompleted) "Completed" else "Incomplete",
                tint = Color.White,
                modifier = Modifier.size(15.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      // Content Column
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(top = 2.dp)
      ) {
        // Title
        Text(
          text = task.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
          color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
          textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        // Description if present
        if (task.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = task.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tags & Metadata Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Priority Tag
          PriorityBadge(priority = task.priority)

          // Category Tag
          CategoryBadge(category = task.category)

          // Due Date Tag
          task.dueDateMillis?.let { dueMillis ->
            DueDateBadge(dueMillis = dueMillis, isCompleted = task.isCompleted)
          }
        }
      }

      // Delete Button
      IconButton(
        onClick = onDelete,
        modifier = Modifier
          .size(40.dp)
          .testTag("task_delete_${task.id}")
      ) {
        Icon(
          imageVector = Icons.Default.DeleteOutline,
          contentDescription = "Delete Task",
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun PriorityBadge(priority: TaskPriority) {
  val (color, container) = when (priority) {
    TaskPriority.HIGH -> PriorityHigh to PriorityHighContainer
    TaskPriority.MEDIUM -> PriorityMedium to PriorityMediumContainer
    TaskPriority.LOW -> PriorityLow to PriorityLowContainer
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = container
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Flag,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(11.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = priority.label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        fontWeight = FontWeight.Bold,
        color = color
      )
    }
  }
}

@Composable
fun CategoryBadge(category: TaskCategory) {
  val icon = getCategoryIcon(category)
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = MaterialTheme.colorScheme.surfaceVariant
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(11.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = category.label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun DueDateBadge(dueMillis: Long, isCompleted: Boolean) {
  val isOverdue = !isCompleted && DateUtils.isOverdue(dueMillis)
  val isToday = DateUtils.isToday(dueMillis)
  val formatted = DateUtils.formatDueDate(dueMillis)

  val (bgColor, textColor) = when {
    isCompleted -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    isOverdue -> PriorityHighContainer to PriorityHigh
    isToday -> PriorityMediumContainer to PriorityMedium
    else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) to MaterialTheme.colorScheme.primary
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = bgColor
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.CalendarToday,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(11.dp)
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = if (isOverdue) "Overdue ($formatted)" else formatted,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
        fontWeight = if (isOverdue || isToday) FontWeight.Bold else FontWeight.Medium,
        color = textColor
      )
    }
  }
}

fun getCategoryIcon(category: TaskCategory): ImageVector {
  return when (category) {
    TaskCategory.PERSONAL -> Icons.Default.Person
    TaskCategory.WORK -> Icons.Default.Work
    TaskCategory.SHOPPING -> Icons.Default.ShoppingCart
    TaskCategory.HEALTH -> Icons.Default.Favorite
    TaskCategory.STUDY -> Icons.Default.School
    TaskCategory.FINANCE -> Icons.Default.AttachMoney
    TaskCategory.OTHER -> Icons.Default.Bookmark
  }
}
