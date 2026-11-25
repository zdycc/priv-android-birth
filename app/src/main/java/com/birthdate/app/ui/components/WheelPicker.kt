package com.birthdate.app.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter

private val ItemHeight = 44.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeightPx = with(LocalDensity.current) { ItemHeight.toPx() }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    )
    val flingBehavior = rememberSnapFlingBehavior(listState)

    // 高亮随滚动实时更新，但不触发父组件重组
    val selectedIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)) }
    }

    // 滚轮停稳后吸附到最近项，并只在此时通知选中结果
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { !it }
            .collect {
                val idx = listState.firstVisibleItemIndex
                val offset = listState.firstVisibleItemScrollOffset
                val target = (if (offset > itemHeightPx / 2) idx + 1 else idx)
                    .coerceIn(0, (items.size - 1).coerceAtLeast(0))
                if (target != listState.firstVisibleItemIndex) {
                    listState.animateScrollToItem(target)
                }
                onSelected(target)
            }
    }

    val background = MaterialTheme.colorScheme.surface

    Box(modifier.height(ItemHeight * 5)) {
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(ItemHeight)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
        )
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(2) { Spacer(Modifier.height(ItemHeight)) }
            items(items.size) { index ->
                Box(
                    Modifier
                        .height(ItemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    val selected = index == selectedIndex
                    Text(
                        text = items[index],
                        style = if (selected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
            items(2) { Spacer(Modifier.height(ItemHeight)) }
        }
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(ItemHeight)
                .background(Brush.verticalGradient(listOf(background, background.copy(alpha = 0f))))
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(ItemHeight)
                .background(Brush.verticalGradient(listOf(background.copy(alpha = 0f), background)))
        )
    }
}
