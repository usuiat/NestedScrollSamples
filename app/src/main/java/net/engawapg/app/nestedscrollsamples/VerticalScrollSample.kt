package net.engawapg.app.nestedscrollsamples

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerticalScrollSample(
) {
    val scrollAreaHeight = 600.dp
    val contentHeight = 1000.dp
    val scrollState = rememberScrollState()
    LazyColumn {
        item { GrayBox() }
        item {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(scrollAreaHeight)
                    .verticalScroll(state = scrollState)
            ) {
                LargeContent(
                    modifier = Modifier.height(contentHeight)
                )
            }
        }
        item { GrayBox() }
    }
}