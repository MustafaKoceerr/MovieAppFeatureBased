package com.mustafakocer.movieappfeaturebasedclean

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.mustafakocer.feature_auth.data.handler.AuthCallbackHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Bu Activity'nin tek görebi, TMDB'den gelen Deep Link'i ('movieapp://auth) yakalamak,
 * içindeki "request_token"'i parse etmek ve ilgili yere illettikten sonra kendini hemen kapatmaktır.
 *
 * Kullanıcı bu activity'yi asla görmez, çünkü theme'i transparandır ve işini çok hızlı bir şekilde yapar.
 */

@AndroidEntryPoint
class AuthCallbackActivity : ComponentActivity() {

    @Inject
    lateinit var authCallbackHandler: AuthCallbackHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AuthCallbackActivity", "Activity created with intent: $intent")

        // Gelen Intent'i ve içindeki URI'ı al.
        val uri = intent.data
        if (uri != null && uri.scheme == "movieapp" && uri.host == "auth") {
            // URI'dan "request_token" query parametresini al.
            val requestToken = uri.getQueryParameter("request_token")
            val isApproved = uri.getQueryParameter("approved") == "true"

            if (isApproved && requestToken != null) {
                // Başarılı bir şekilde onaylanmış ve token alınmış.
                Log.d("AuthCallbackActivity", "Authentication approved. Token: $requestToken")
                // Token'ı merkezi Handler'a gönder.
                authCallbackHandler.onNewTokenReceived(requestToken)
            } else {
                Log.w(
                    "AuthCallbackActivity",
                    "Authentication was not approved or token is missing."
                )
                // Kullanıcı reddetti veya bir hata oluştu.
                // Bu durumu da Handler üzerinden bildirebiliriz (şimdilik logluyoruz).
                // authCallbackHandler.onAuthCancelled()
            }
        } else {
            Log.e("AuthCallbackActivity", "Invalid URI received for auth callback: $uri")

        }

        // Bu Activity'nin görevi bitti, ana uygulamaya dön ve kendini kapat.
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        startActivity(mainIntent)
        finish()
    }
}

/**
 * Bu pattern neden single activity pattern'i bozmuyor?
 *
 * Bu, mimari tutarlılık açısından sorulması gereken harika ve çok yerinde bir soru.
 * Kısa cevap: Hayır, bu implementasyon Single Activity Pattern'i bozmuyor, aksine onu destekliyor ve güçlendiriyor.
 * Şimdi nedenini açıklayalım.
 * Single Activity Pattern'in Ruhu Nedir?
 * Single Activity Pattern'in (SAP) temel felsefesi, uygulamanın tüm UI'ını ve ekranlarını tek bir Activity (MainActivity) içinde, Fragment'lar veya (bizim durumumuzda) Jetpack Compose'un NavController'ı ile yönetmektir.
 * Bu desenin amacı, Android'in karmaşık Activity yaşam döngüleri, Intent'ler arası veri geçişi ve task stack yönetimi gibi zorluklarından kaçınmaktır. Her şey tek bir Activity'nin çatısı altında, daha basit ve öngörülebilir bir şekilde çalışır.
 * Bizim AuthCallbackActivity'miz Neden Bu Deseni Bozmuyor?
 * AuthCallbackActivity, Single Activity Pattern'in ruhuna aykırı bir "ekran" değildir. Çünkü:
 * UI'ı Yoktur (Görünmezdir): Bu Activity'nin bir arayüzü yoktur. Teması transparandır ve onCreate'de setContent'i çağırmaz. Kullanıcı onu asla bir "ekran" olarak görmez. O, sadece arka planda çalışan bir "işleyici"dir.
 * Kalıcı Değildir: onCreate'de işini yapar yapmaz finish() metodunu çağırarak kendini anında yok eder. Uygulamanın navigasyon yığınına (back stack) asla girmez. Kullanıcı geri tuşuna bastığında bu Activity'ye dönemez.
 * Tek Bir Görevi Vardır (Single Responsibility): Görevi, uygulamanın dışında gerçekleşen bir olayı (tarayıcıdan gelen bir Deep Link) yakalayıp, bu olayın sonucunu Single Activity'mizin (MainActivity) dünyasına (bizim durumumuzda, paylaşılan bir ViewModel veya Handler aracılığıyla) iletmektir. O, dış dünya ile iç dünyamız arasında bir köprüdür.
 * Bir Analoji:
 * MainActivity'yi, içinde tüm odaların (Composable ekranlar) bulunduğu büyük bir ev olarak düşün. AuthCallbackActivity ise, dışarıdan gelen bir postayı (Deep Link) alıp, kapıdan içeri uzatıp, "Bu mektup size geldi" dedikten sonra ortadan kaybolan bir postacı gibidir. Postacı, evin bir parçası değildir veya evin içinde yaşamaz. Sadece dışarıdan bir bilgiyi içeriye aktarır.
 * Best Practice Olarak Neden Tercih Edilir?
 * Bu "görünmez Activity köprüsü" yöntemi, Single Activity Pattern ile çalışan uygulamalarda, dış dünyadan gelen Intent'leri (Deep Link, Push Notification, vb.) işlemenin en temiz ve en standart yoludur.
 * Çünkü bu sayede MainActivity'miz, bu tür dış olayların karmaşıklığıyla kirlenmemiş olur. MainActivity'nin onNewIntent gibi metotlarını override edip, gelen Intent'in bir Deep Link mi, bir bildirim mi, yoksa başka bir şey mi olduğunu anlamaya çalışan karmaşık if/else blokları yazmak zorunda kalmayız. Her dış olay türü, kendi küçük, odaklanmış ve "tek kullanımlık" Activity'si tarafından ele alınır.
 * Sonuç:
 * Bu implementasyon, Single Activity Pattern'i bozmaz. Tam tersine, MainActivity'yi temiz tutarak ve sorumlulukları ayırarak bu deseni daha sürdürülebilir bir şekilde uygulamamızı sağlar.
 */