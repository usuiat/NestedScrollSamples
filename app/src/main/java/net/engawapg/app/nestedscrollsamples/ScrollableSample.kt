package net.engawapg.app.nestedscrollsamples

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableSample() {
    val scrollAreaHeight = 600.dp
    val contentHeight = 1000.dp
    val offsetMax = with(LocalDensity.current) { (contentHeight - scrollAreaHeight).toPx() }
    var offset by remember { mutableFloatStateOf(0f) }

    val scrollableState = rememberScrollableState { delta ->
        val oldOffset = offset
        offset = (oldOffset + delta).coerceIn(-offsetMax, 0f)
        offset - oldOffset
    }

    LazyColumn {
        item { GrayBox() }
        item {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(scrollAreaHeight)
                    .clipToBounds()
                    .scrollable(
                        state = scrollableState,
                        orientation = Orientation.Vertical,
                    )
            ) {
                LargeContent(
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.Top, unbounded = true)
                        .height(contentHeight)
                        .graphicsLayer { translationY = offset }
                )
            }
        }
        item { GrayBox() }
    }
}