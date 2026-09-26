package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PriorityLow
import com.example.ui.viewmodel.TaskUiState
import com.example.util.DateUtils

@Composable
fun TaskSummaryHeader(
  uiState: TaskUiState,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = uiState.progress,
    animationSpec = tween(durationMillis = 600),
    label = "progress"
  )

  val todayFormatted = DateUtils.formatFullDate(System.currentTimeMillis())

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    // Date & Greeting
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = todayFormatted.uppercase(),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = if (uiState.totalCount == 0) "Welcome!" else if (uiState.completedCount == uiState.totalCount) "All caught up! 🎉" else "Today's Focus",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onBackground
        )
      }

      // Completion badge
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.testTag("completion_badge")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Progress Card
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .testTag("summary_card"),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
      ),
      shape = RoundedCornerShape(20.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (uiState.totalCount == 0) "No tasks yet" else "${uiState.completedCount} of ${uiState.totalCount} completed",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = when {
                uiState.totalCount == 0 -> "Add your first task to start organizing"
                uiState.completedCount == uiState.totalCount -> "Great job completing your tasks!"
                uiState.pendingCount == 1 -> "Almost there! Only 1 task left"
                else -> "${uiState.pendingCount} tasks remaining"
              },
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(46.dp)
          ) {
            CircularProgressIndicator(
              progress = { 1f },
              modifier = Modifier.size(46.dp),
              color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
              strokeWidth = 4.dp
            )
            CircularProgressIndicator(
              progress = { animatedProgress },
              modifier = Modifier.size(46.dp),
              color = MaterialTheme.colorScheme.primary,
              strokeWidth = 4.dp,
              strokeCap = StrokeCap.Round
            )
            Text(
              text = "${uiState.completedCount}/${uiState.totalCount}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Linear Progress bar
        LinearProgressIndicator(
          progress = { animatedProgress },
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
          color = MaterialTheme.colorScheme.primary,
          trackColor = MaterialTheme.colorScheme.surface,
          strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Stats Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          StatChip(
            icon = Icons.Default.Schedule,
            label = "Due Today",
            count = uiState.todayCount,
            accentColor = MaterialTheme.colorScheme.primary
          )
          StatChip(
            icon = Icons.Default.Flag,
            label = "High Priority",
            count = uiState.highPriorityCount,
            accentColor = PriorityHigh
          )
          StatChip(
            icon = Icons.Default.CheckCircle,
            label = "Done",
            count = uiState.completedCount,
            accentColor = PriorityLow
          )
        }
      }
    }
  }
}

@Composable
private fun StatChip(
  icon: ImageVector,
  label: String,
  count: Int,
  accentColor: Color
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(10.dp))
      .background(MaterialTheme.colorScheme.surface)
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      modifier = Modifier.size(14.dp),
      tint = accentColor
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = "$label: ",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = count.toString(),
      style = MaterialTheme.typography.labelSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}
