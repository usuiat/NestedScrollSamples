package net.engawapg.app.nestedscrollsamples

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.util.VelocityTracker1D
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DispatcherSample2() {
    val scrollAreaHeight = 600.dp
    val contentHeight = 1000.dp
    val offsetMax = with(LocalDensity.current) { (contentHeight - scrollAreaHeight).toPx() }
    val scrollState = remember { DispatcherSampleScrollStateImpl2(offsetMax = offsetMax) }
    DispatcherSampleScreen(
        scrollState = scrollState,
        scrollAreaHeight = scrollAreaHeight,
        contentHeight = contentHeight,
    )
}

@Stable
class DispatcherSampleScrollStateImpl2(
    offsetMax: Float,
) : DispatcherSampleScrollState {
    private val _offset = Animatable(0f).apply { updateBounds(-offsetMax, 0f) }
    override val offset: Float
        get() = _offset.value

    override val nestedScrollConnection = object : NestedScrollConnection {}
    override val nestedScrollDispatcher = NestedScrollDispatcher()

    private var componentPosition = 0f
    override fun updateComponentPosition(position: Float) {
        componentPosition = position
    }

    private val velocityTracker = VelocityTracker1D(false)

    override fun reset() {
        velocityTracker.resetTracking()
    }

    override suspend fun drag(delta: Float, position: Float, timeMillis: Long) {
        val available = Offset(0f, delta)
        val preConsumed = nestedScrollDispatcher.dispatchPreScroll(
            available = available,
            source = NestedScrollSource.Drag,
        )

        val weAvailable = available - preConsumed
        val oldY = offset
        _offset.snapTo(offset + weAvailable.y)
        val weConsumed = Offset(0f, offset - oldY)

        nestedScrollDispatcher.dispatchPostScroll(
            consumed = preConsumed + weConsumed,
            available = available - preConsumed - weConsumed,
            source = NestedScrollSource.Drag,
        )

        velocityTracker.addDataPoint(timeMillis, position + componentPosition)
    }

    override suspend fun fling() {
        val velocityY = velocityTracker.calculateVelocity()
        val velocity = Velocity(0f, velocityY)
        val preConsumed = nestedScrollDispatcher.dispatchPreFling(
            available = velocity,
        )

        val animationResult = _offset.animateDecay(
            initialVelocity = velocityY - preConsumed.y,
            animationSpec = exponentialDecay(),
        )

        if (animationResult.endReason == AnimationEndReason.BoundReached) {
            val postAvailable = Velocity(0f, animationResult.endState.velocity)
            nestedScrollDispatcher.coroutineScope.launch {
                nestedScrollDispatcher.dispatchPostFling(
                    consumed = velocity - postAvailable,
                    available = postAvailable,
                )
            }
        }
    }
}
