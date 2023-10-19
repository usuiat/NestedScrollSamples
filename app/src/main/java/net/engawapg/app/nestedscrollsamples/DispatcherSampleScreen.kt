package net.engawapg.app.nestedscrollsamples

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

interface DispatcherSampleScrollState {
    val nestedScrollConnection: NestedScrollConnection
    val nestedScrollDispatcher: NestedScrollDispatcher
    val offset: Float

    fun reset()
    suspend fun drag(delta: Float, position: Float, timeMillis: Long)
    suspend fun fling()
    fun updateComponentPosition(position: Float)
}

@Composable
fun DispatcherSampleScreen(
    scrollState: DispatcherSampleScrollState,
    scrollAreaHeight: Dp,
    contentHeight: Dp,
) {
    LazyColumn {
        item { GrayBox() }
        item {
            val scope = rememberCoroutineScope()
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(scrollAreaHeight)
                    .clipToBounds()
                    .nestedScroll(
                        connection = scrollState.nestedScrollConnection,
                        dispatcher = scrollState.nestedScrollDispatcher,
                    )
                    .onGloballyPositioned { coordinates ->
                        scrollState.updateComponentPosition(coordinates.positionInWindow().y)
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                scrollState.reset()
                            },
                            onDrag = { change, dragAmount ->
                                scope.launch {
                                    scrollState.drag(
                                        delta = dragAmount.y,
                                        position = change.position.y,
                                        timeMillis = change.uptimeMillis
                                    )
                                }
                            },
                            onDragEnd = {
                                scope.launch {
                                    scrollState.fling()
                                }
                            },
                        )
                    }
            ) {
                LargeContent(
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.Top, unbounded = true)
                        .height(contentHeight)
                        .graphicsLayer { translationY = scrollState.offset }
)
            }
        }
        item { GrayBox() }
    }
}