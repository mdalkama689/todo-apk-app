package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskCategory
import com.example.ui.viewmodel.TaskFilter
import com.example.ui.viewmodel.TaskSort

@Composable
fun FilterBar(
  selectedFilter: TaskFilter,
  onFilterSelected: (TaskFilter) -> Unit,
  selectedCategory: TaskCategory?,
  onCategorySelected: (TaskCategory?) -> Unit,
  selectedSort: TaskSort,
  onSortSelected: (TaskSort) -> Unit,
  modifier: Modifier = Modifier
) {
  var sortMenuExpanded by remember { mutableStateOf(false) }

  Column(modifier = modifier.fillMaxWidth()) {
    // Primary Tab Row
    val filters = TaskFilter.entries
    val selectedIndex = filters.indexOf(selectedFilter)

    TabRow(
      selectedTabIndex = selectedIndex,
      containerColor = MaterialTheme.colorScheme.background,
      contentColor = MaterialTheme.colorScheme.primary,
      indicator = { tabPositions ->
        if (selectedIndex in tabPositions.indices) {
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
            color = MaterialTheme.colorScheme.primary,
            height = 3.dp
          )
        }
      },
      divider = {}
    ) {
      filters.forEach { filter ->
        val selected = filter == selectedFilter
        Tab(
          selected = selected,
          onClick = { onFilterSelected(filter) },
          text = {
            Text(
              text = filter.label,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 14.sp
            )
          },
          modifier = Modifier.testTag("tab_${filter.name.lowercase()}")
        )
      }
    }

    // Secondary row: Categories & Sort button
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Horizontal scrollable categories
      Row(
        modifier = Modifier
          .weight(1f)
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // "All" Category Chip
        FilterChip(
          selected = selectedCategory == null,
          onClick = { onCategorySelected(null) },
          label = { Text("All Categories", fontSize = 12.sp) },
          shape = RoundedCornerShape(16.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
          ),
          modifier = Modifier.testTag("chip_cat_all")
        )

        TaskCategory.entries.forEach { category ->
          val selected = selectedCategory == category
          FilterChip(
            selected = selected,
            onClick = { onCategorySelected(if (selected) null else category) },
            label = { Text(category.label, fontSize = 12.sp) },
            leadingIcon = {
              Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
            },
            shape = RoundedCornerShape(16.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("chip_cat_${category.name.lowercase()}")
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Sort Menu Button
      Box {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          onClick = { sortMenuExpanded = true },
          modifier = Modifier.testTag("sort_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Sort,
              contentDescription = "Sort",
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = selectedSort.label,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        DropdownMenu(
          expanded = sortMenuExpanded,
          onDismissRequest = { sortMenuExpanded = false }
        ) {
          TaskSort.entries.forEach { sort ->
            DropdownMenuItem(
              text = {
                Text(
                  text = sort.label,
                  fontWeight = if (sort == selectedSort) FontWeight.Bold else FontWeight.Normal,
                  color = if (sort == selectedSort) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
              },
              onClick = {
                onSortSelected(sort)
                sortMenuExpanded = false
              },
              modifier = Modifier.testTag("sort_item_${sort.name.lowercase()}")
            )
          }
        }
      }
    }
  }
}
