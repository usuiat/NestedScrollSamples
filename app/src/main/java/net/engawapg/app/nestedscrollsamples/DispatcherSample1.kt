package net.engawapg.app.nestedscrollsamples

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.util.VelocityTracker1D
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun DispatcherSample1() {
    val scrollAreaHeight = 600.dp
    val contentHeight = 1000.dp
    val offsetMax = with(LocalDensity.current) { (contentHeight - scrollAreaHeight).toPx() }
    val scrollState = remember { DispatcherSampleScrollStateImpl1(offsetMax = offsetMax) }
    DispatcherSampleScreen(
        scrollState = scrollState,
        scrollAreaHeight = scrollAreaHeight,
        contentHeight = contentHeight,
    )
}

@Stable
class DispatcherSampleScrollStateImpl1(
    private val offsetMax: Float,
) : DispatcherSampleScrollState {
    private val _offset = mutableFloatStateOf(0f)
    override val offset: Float
        get() = _offset.floatValue

    override val nestedScrollConnection = object : NestedScrollConnection {}
    override val nestedScrollDispatcher = NestedScrollDispatcher()

    private var componentPosition = 0f
    override fun updateComponentPosition(position: Float) {
        componentPosition = position
    }

    private val velocityTracker = VelocityTracker1D(false)
    private var cancelFling = false

    override fun reset() {
        velocityTracker.resetTracking()
    }

    override suspend fun drag(delta: Float, position: Float, timeMillis: Long) {
        cancelFling = true
        scroll(delta, NestedScrollSource.Drag)
        val positionInWindow = position + componentPosition
        velocityTracker.addDataPoint(timeMillis, positionInWindow)
    }

    override suspend fun fling() {
        val velocityY = velocityTracker.calculateVelocity()
        cancelFling = false
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocityY,
        ).animateDecay(exponentialDecay()) {
            if (cancelFling) {
                cancelAnimation()
            }
            scroll(value - lastValue, NestedScrollSource.Fling)
            lastValue = value
        }
    }

    private fun scroll(delta: Float, source: NestedScrollSource) {
        if (delta == 0f) return

        val available = Offset(0f, delta)
        val preConsumed = nestedScrollDispatcher.dispatchPreScroll(
            available = available,
            source = source,
        )

        val weAvailable = available - preConsumed
        val newY = (offset + weAvailable.y).coerceIn(-offsetMax, 0f)
        val weConsumed = Offset(0f, newY - offset)
        _offset.floatValue = newY

        nestedScrollDispatcher.dispatchPostScroll(
            consumed = preConsumed + weConsumed,
            available = available - preConsumed - weConsumed,
            source = source,
        )
    }
}
