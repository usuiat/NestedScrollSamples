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

interface ConnectionSampleScrollState {
    val nestedScrollConnection: NestedScrollConnection
    val offset: Dp
}

@Composable
fun ConnectionSampleScreen(scrollState: ConnectionSampleScrollState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollState.nestedScrollConnection)
    ) {
        GrayBox(modifier = Modifier.height(scrollState.offset))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = scrollState.offset)
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
