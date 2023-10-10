package net.engawapg.app.nestedscrollsamples

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

interface ConnectionSampleState {
    val connection: NestedScrollConnection
    val offset: Dp
}

@Composable
fun ConnectionSampleScreen(state: ConnectionSampleState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(state.connection)
    ) {
        GrayBox(modifier = Modifier.height(state.offset))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = state.offset)
        ) {
            items(50) {
                Text(
                    text = "Item $it",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
            }
        }
    }
}
