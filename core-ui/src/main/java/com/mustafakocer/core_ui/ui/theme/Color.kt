package com.mustafakocer.core_ui.ui.theme

import androidx.compose.ui.graphics.Color

// Ana Marka ve Vurgu Renkleri
val BrandGold =
    Color(0xFFFFC107)      // Altın Sarısı: Rating yıldızları, premium özellikler, ana butonlar için
val BrandBlue = Color(0xFF0D47A1)      // Derin Mavi: Linkler, ikincil butonlar için

// Dark Theme (Sinematik Gece)
val DarkPrimary = Color(0xFF1A2C42)         // Ana Renk: Gece Mavisi
val DarkOnPrimary = Color(0xFFFFFFFF)       // Ana Renk Üzeri: Beyaz
val DarkPrimaryContainer = Color(0xFF2C3E50) // Ana Konteyner: Biraz daha açık mavi-gri
val DarkOnPrimaryContainer = Color(0xFFE0E0E0) // Konteyner Üzeri: Açık Gri

val DarkSecondary = BrandGold               // İkincil Renk: Marka Altın Sarısı
val DarkOnSecondary = Color(0xFF000000)     // İkincil Renk Üzeri: Siyah (kontrast için)
val DarkSecondaryContainer = Color(0x33FFC107) // %20 Opaklıkta Altın Sarısı (vurgu için)
val DarkOnSecondaryContainer = BrandGold    // Konteyner Üzeri: Marka Altın Sarısı

val DarkBackground = Color(0xFF0F172A)      // Zemin: Neredeyse siyah, hafif mavi tonlu
val DarkOnBackground = Color(0xFFE2E8F0)    // Zemin Üzeri Metin: Çok açık gri
val DarkSurface = Color(0xFF1E293B)         // Yüzey (Kartlar): Koyu Kurşun Rengi
val DarkOnSurface = Color(0xFFCBD5E1)       // Yüzey Üzeri Metin: Açık gri
val DarkSurfaceVariant = Color(0xFF334155)  // Yüzey Varyantı: Daha açık kurşun rengi
val DarkOnSurfaceVariant = Color(0xFF94A3B8) // Yüzey Varyantı Üzeri Metin: Soluk gri

val DarkError = Color(0xFFEF4444)           // Hata: Canlı Kırmızı
val DarkOnError = Color(0xFFFFFFFF)         // Hata Üzeri: Beyaz

// Light Theme (Temiz Stüdyo Işığı)
val LightPrimary = Color(0xFF2C3E50)         // Ana Renk: Koyu Mavi-Gri
val LightOnPrimary = Color(0xFFFFFFFF)      // Ana Renk Üzeri: Beyaz
val LightPrimaryContainer = Color(0xFFD6EAF8) // Ana Konteyner: Çok açık mavi
val LightOnPrimaryContainer = Color(0xFF1A2C42) // Konteyner Üzeri: Gece Mavisi

val LightSecondary = BrandGold              // İkincil Renk: Marka Altın Sarısı
val LightOnSecondary = Color(0xFF000000)     // İkincil Renk Üzeri: Siyah
val LightSecondaryContainer = Color(0x33FFC107) // %20 Opaklıkta Altın Sarısı
val LightOnSecondaryContainer = Color(0xFFB8860B) // Konteyner Üzeri: Koyu Altın

val LightBackground = Color(0xFFF8FAFC)     // Zemin: Çok çok açık gri
val LightOnBackground = Color(0xFF0F172A)   // Zemin Üzeri Metin: Gece Mavisi
val LightSurface = Color(0xFFFFFFFF)        // Yüzey (Kartlar): Beyaz
val LightOnSurface = Color(0xFF1E293B)      // Yüzey Üzeri Metin: Koyu Kurşun Rengi
val LightSurfaceVariant = Color(0xFFE2E8F0) // Yüzey Varyantı: Açık Gri
val LightOnSurfaceVariant = Color(0xFF475569) // Yüzey Varyantı Üzeri Metin: Orta Gri

val LightError = Color(0xFFB91C1C)           // Hata: Koyu Kırmızı
val LightOnError = Color(0xFFFFFFFF)        // Hata Üzeri: Beyaz