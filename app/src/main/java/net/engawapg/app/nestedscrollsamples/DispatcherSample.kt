package net.engawapg.app.nestedscrollsamples

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DispatcherSample() {
    LazyColumn {
        item { GrayBox() }
        item {
            val offsetMax = with(LocalDensity.current) { 400.dp.toPx() }
            val scrollState = rememberVerticalScrollState(
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
                    .onGloballyPositioned { coordinates ->
                        scrollState.positionInWindow = coordinates.positionInWindow().y
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = {
                                scrollState.resetTracking()
                            },
                            onDrag = { change, dragAmount ->
                                scope.launch {
                                    scrollState.drag(
                                        dy = dragAmount.y,
                                        positionY = change.position.y,
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
                        .height(1000.dp)
                        .graphicsLayer { translationY = scrollState.y }
                )
            }
        }
        item { GrayBox() }
    }
}

@Composable
fun rememberVerticalScrollState(
    lowerBound: Float,
    upperBound: Float,
    initialValue: Float,
) = remember {
    VerticalScrollState(
        lowerBound = lowerBound,
        upperBound = upperBound,
        initialValue = initialValue,
    )
}

class VerticalScrollState(
    lowerBound: Float,
    upperBound: Float,
    initialValue: Float,
) {
    private val _y = Animatable(initialValue).apply { updateBounds(lowerBound, upperBound) }
    val y: Float
        get() = _y.value
    val connection = object : NestedScrollConnection {}
    val dispatcher = NestedScrollDispatcher()
    private val velocityTracker = VelocityTracker1D(false)
    var positionInWindow = 0f

    fun resetTracking() {
        velocityTracker.resetTracking()
    }

    suspend fun drag(dy: Float, positionY: Float, timeMillis: Long) {
        val delta = Offset(0f, dy)
        val preConsumed = dispatcher.dispatchPreScroll(
            available = delta,
            source = NestedScrollSource.Drag,
        )

        val weAvailable = delta - preConsumed
        val oldY = y
        _y.snapTo(y + weAvailable.y)
        val weConsumed = Offset(0f, y - oldY)

        dispatcher.dispatchPostScroll(
            consumed = preConsumed + weConsumed,
            available = delta - preConsumed - weConsumed,
            source = NestedScrollSource.Drag,
        )

        velocityTracker.addDataPoint(timeMillis, positionY + positionInWindow)
    }

    suspend fun fling() {
        val velocityY = velocityTracker.calculateVelocity()
        val velocity = Velocity(0f, velocityY)
        val preConsumed = dispatcher.dispatchPreFling(
            available = velocity,
        )

        val animationResult = _y.animateDecay(
            initialVelocity = velocityY - preConsumed.y,
            animationSpec = exponentialDecay(),
        )

        if (animationResult.endReason == AnimationEndReason.BoundReached) {
            val postAvailable = Velocity(0f, animationResult.endState.velocity)
            dispatcher.coroutineScope.launch {
                dispatcher.dispatchPostFling(
                    consumed = velocity - postAvailable,
                    available = postAvailable,
                )
            }
        }
    }
}
