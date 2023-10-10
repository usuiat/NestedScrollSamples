package net.engawapg.app.nestedscrollsamples

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker1D
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DispatcherSample2() {
    LazyColumn {
        item { GrayBox() }
        item {
            val offsetMax = with(LocalDensity.current) { 400.dp.toPx() }
            val scrollState = rememberVerticalScrollState2(
                lowerBound = -offsetMax,
                upperBound = 0f,
                initialValue = 0f,
            )
            val scope = rememberCoroutineScope()
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .clipToBounds()
                    .nestedScroll(
                        connection = scrollState.connection,
                        dispatcher = scrollState.dispatcher,
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                scrollState.resetTracking()
                            },
                            onDrag = { change, dragAmount ->
                                scrollState.drag(
                                    dy = dragAmount.y,
                                    positionY = change.position.y,
                                    timeMillis = change.uptimeMillis
                                )
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
                        .height(1000.dp)
                        .graphicsLayer { translationY = scrollState.y }
                )
            }
        }
        item { GrayBox() }
    }
}

@Composable
fun rememberVerticalScrollState2(
    lowerBound: Float,
    upperBound: Float,
    initialValue: Float,
) = remember {
    VerticalScrollState2(
        lowerBound = lowerBound,
        upperBound = upperBound,
        initialValue = initialValue,
    )
}

class VerticalScrollState2(
    private val lowerBound: Float,
    private val upperBound: Float,
    initialValue: Float,
) {
    private val _y = mutableFloatStateOf(initialValue)
    val y: Float
        get() = _y.floatValue
    val connection = object : NestedScrollConnection {}
    val dispatcher = NestedScrollDispatcher()
    private val velocityTracker = VelocityTracker1D(false)
    private var cancelFling = false

    fun resetTracking() {
        velocityTracker.resetTracking()
    }

    fun drag(dy: Float, positionY: Float, timeMillis: Long) {
        cancelFling = true
        scroll(dy, NestedScrollSource.Drag)
        velocityTracker.addDataPoint(timeMillis, positionY)
    }

    suspend fun fling() {
        val velocityY = velocityTracker.calculateVelocity()
        cancelFling = false
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocityY,
        ).animateDecay(exponentialDecay()) {
            if (cancelFling) {
                println("*** CANCEL ANIMATION!!!!!")
                cancelAnimation()
            }
            scroll(value - lastValue, NestedScrollSource.Fling)
            lastValue = value
        }
    }

    private fun scroll(dy: Float, source: NestedScrollSource) {
        if (dy == 0f) return

        val delta = Offset(0f, dy)
        val preConsumed = dispatcher.dispatchPreScroll(
            available = delta,
            source = source,
        )

        val weAvailable = delta - preConsumed
        val newY = (y + weAvailable.y).coerceIn(lowerBound, upperBound)
        val weConsumed = Offset(0f, newY - y)
        _y.floatValue = newY

        dispatcher.dispatchPostScroll(
            consumed = preConsumed + weConsumed,
            available = delta - preConsumed - weConsumed,
            source = source,
        )
    }
}
