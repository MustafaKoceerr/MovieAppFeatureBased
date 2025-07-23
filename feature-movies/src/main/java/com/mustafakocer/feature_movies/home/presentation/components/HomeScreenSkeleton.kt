package com.mustafakocer.feature_movies.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.loading.ShimmerBrush

@Composable
fun HomeScreenSkeleton() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Fake Search Bar Skeleton
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(ShimmerBrush())
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Category Skeletons
        repeat(3) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.4f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ShimmerBrush())
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ShimmerBrush())
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}