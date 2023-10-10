package net.engawapg.app.nestedscrollsamples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionSample1() {
    val state = rememberConnectionSampleState1(maxY = 200.dp, initY = 200.dp)
    ConnectionSampleScreen(state = state)
}

@Composable
fun rememberConnectionSampleState1(
    maxY: Dp,
    initY: Dp,
): ConnectionSampleState {
    val density = LocalDensity.current
    return remember {
        ConnectionSampleStateImpl1(
            maxOffset = maxY,
            initialOffset = initY,
            density = density,
        )
    }
}

@Stable
class ConnectionSampleStateImpl1(
    maxOffset: Dp,
    initialOffset: Dp,
    private val density: Density,
) : ConnectionSampleState {
    private val maxOffsetPx = with(density) { maxOffset.toPx() }
    private val initialOffsetPx = with(density) { initialOffset.toPx() }
    private var _offsetPx by mutableFloatStateOf(initialOffsetPx)
    override val offset: Dp
        get() = with(density) { _offsetPx.toDp() }

    override val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if ((available.y >= 0f) or (_offsetPx <= 0f)) return Offset.Zero
            val consumedY = doScroll(available.y)
            return Offset(0f, consumedY)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if ((available.y <= 0f) or (_offsetPx >= maxOffsetPx)) return Offset.Zero
            val consumedY = doScroll(available.y)
            return Offset(0f, consumedY)
        }
    }

    private fun doScroll(delta: Float): Float {
        val oldOffset = _offsetPx
        _offsetPx = (_offsetPx + delta).coerceIn(0f, maxOffsetPx)
        return _offsetPx - oldOffset
    }
}

