package com.mustafakocer.core_ui.component.loading


import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

/**
 * Ekran boyutuna ve verilen öğe boyutuna göre dinamik olarak bir iskelet (skeleton)
 * listesi oluşturan ve üzerinde shimmer efekti gösteren, son derece esnek bir yükleme ekranı.
 *
 * @param modifier Bu bileşene uygulanacak olan Modifier.
 * @param itemHeight Gösterilecek her bir iskelet öğesinin yüksekliği.
 * @param itemWidth Gösterilecek her bir iskelet öğesinin genişliği.
 * @param orientation İskelet listesinin yönelimi (Dikey veya Yatay).
 * @param contentPadding Liste kenarlarında bırakılacak boşluk.
 * @param skeletonContent Yükleme sırasında gösterilecek olan tek bir iskelet Composable'ı.
 */
@Composable
fun ShimmerLoadingScreen(
    modifier: Modifier = Modifier,
    itemHeight: Dp,
    itemWidth: Dp, // <-- Varsayılan değer kaldırıldı, artık zorunlu.
    orientation: Orientation = Orientation.Vertical,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    skeletonContent: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val itemCount = remember(configuration, itemHeight, itemWidth, orientation) {
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
        val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
        val itemHeightPx = with(density) { itemHeight.toPx() }
        val itemWidthPx = with(density) { itemWidth.toPx() }

        if (orientation == Orientation.Vertical) {
            // Dikey liste için, yüksekliğe göre hesapla.
            ceil(screenHeightPx / itemHeightPx).toInt() + 1
        } else {
            // Yatay liste için, genişliğe göre hesapla.
            // Genişlik 0'dan büyük olmalı.
            if (itemWidthPx > 0) {
                ceil(screenWidthPx / itemWidthPx).toInt() + 1
            } else {
                10 // Genişlik belirtilmemişse varsayılan bir sayı döndür.
            }
        }
    }

    if (orientation == Orientation.Vertical) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            userScrollEnabled = false
        ) {
            items(itemCount) {
                skeletonContent()
            }
        }
    } else {
        LazyRow(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            userScrollEnabled = false
        ) {
            items(itemCount) {
                skeletonContent()
            }
        }
    }
}