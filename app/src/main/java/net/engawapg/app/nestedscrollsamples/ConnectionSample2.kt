package net.engawapg.app.nestedscrollsamples

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.splineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking

@Composable
fun ConnectionSample2() {
    val density = LocalDensity.current
    val scrollState = remember {
        ConnectionSampleScrollStateImpl2(
            maxOffset = 200.dp,
            initialOffset = 200.dp,
            density = density,
        )
    }
    ConnectionSampleScreen(scrollState = scrollState)
}

@Stable
class ConnectionSampleScrollStateImpl2(
    maxOffset: Dp,
    initialOffset: Dp,
    private val density: Density,
) : ConnectionSampleScrollState {
    private val maxOffsetPx = with(density) { maxOffset.toPx() }
    private val initialOffsetPx = with(density) { initialOffset.toPx() }
    private var _offsetPx = Animatable(initialValue = initialOffsetPx).apply {
        updateBounds(0f, maxOffsetPx)
    }
    override val offset: Dp
        get() = with(density) { _offsetPx.value.toDp() }

    override val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (source == NestedScrollSource.Fling) return Offset.Zero
            if ((available.y >= 0f) or (_offsetPx.value <= 0f)) return Offset.Zero
            val consumed = doScroll(available.y)
            return Offset(0f, consumed)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (source == NestedScrollSource.Fling) return Offset.Zero
            if ((available.y <= 0f) or (_offsetPx.value >= maxOffsetPx)) return Offset.Zero
            val consumedY = doScroll(available.y)
            return Offset(0f, consumedY)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if ((available.y >= 0f) or (_offsetPx.value <= 0f)) return Velocity.Zero
            val consumedY = doFling(available.y)
            return Velocity(0f, consumedY)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if ((available.y <= 0f) or (_offsetPx.value >= maxOffsetPx)) return Velocity.Zero
            val consumedY = doFling(available.y)
            return Velocity(0f, consumedY)
        }
    }

    private fun doScroll(delta: Float): Float {
        val oldOffset = _offsetPx.value
        runBlocking {
            _offsetPx.snapTo(_offsetPx.value + delta)
        }
        return _offsetPx.value - oldOffset
    }

    private suspend fun doFling(velocity: Float): Float {
        val result = _offsetPx.animateDecay(
            initialVelocity = velocity,
            animationSpec = splineBasedDecay(density)
        )
        if (result.endReason == AnimationEndReason.BoundReached) {
            return (velocity - result.endState.velocity)
        }
        return velocity
    }
}
