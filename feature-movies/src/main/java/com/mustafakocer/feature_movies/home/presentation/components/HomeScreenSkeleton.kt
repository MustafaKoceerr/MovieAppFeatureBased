import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.loading.ShimmerBrush

/**
 * A composable that displays a skeleton loading UI for the HomeScreen.
 *
 * This component is shown during the initial data fetch to provide a visual placeholder
 * that mimics the layout of the actual home screen content. It improves the user's perceived
 * performance by giving an immediate impression of the app's structure before the data is available.
 */
@Composable
fun HomeScreenSkeleton() {
    // The root column organizes the skeleton layout vertically, matching the real screen's structure.
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // Skeleton placeholder for the FakeSearchBar.
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(ShimmerBrush()) // The shimmer provides an animated loading gradient.
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Skeleton placeholders for multiple movie category rows.
        // The `repeat(3)` count is a pragmatic choice to simulate a typical screen's content length
        // without needing to know the actual number of categories beforehand.
        repeat(3) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Placeholder for the category title.
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .fillMaxWidth(0.4f) // Mimics the approximate length of a category title.
                        .clip(RoundedCornerShape(4.dp))
                        .background(ShimmerBrush())
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Placeholders for the horizontal row of movie posters.
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