package net.engawapg.app.nestedscrollsamples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun GrayBox(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
    ) {
        Text(
            text = "GrayBox",
            color = Color.Black
        )
    }
}

@Composable
fun LargeContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Text(
            text = "Top of LargeContent",
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
        Text(
            text = "Center of LargeContent",
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
        )
        Text(
            text = "Bottom of LargeContent",
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}
