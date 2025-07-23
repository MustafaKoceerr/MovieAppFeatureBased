package com.mustafakocer.feature_auth.welcome.presentation.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.feature_auth.R
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeUiState
import com.mustafakocer.feature_auth.welcome.presentation.screen.WelcomeScreen
import com.mustafakocer.core_ui.R.drawable.img_app_logo as appLogo

@Composable
fun WelcomeContent(
    state: WelcomeUiState,
    onLoginClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Ekrana giriş animasyonlarını tetikle
    LaunchedEffect(Unit) {
        startAnimation = true
    }

    // ANİMASYON DEĞERLERİ BURADA TANIMLANIYOR
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "logo_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logo_alpha"
    )

    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo (Animasyon modifier'ları ile birlikte)
        AppLogo(
            modifier = Modifier
                .scale(scale)
                .alpha(alpha)
        )
        Spacer(modifier = Modifier.height(48.dp))

        // Title and Subtitle (Bunları da ayrıca anime edebiliriz)
        Column(
            modifier = Modifier.alpha(alpha), // Metin grubuna da fade-in uygulayalım
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(64.dp))

        // Action Buttons (Bunları da ayrıca anime edebiliriz)
        Column(
            modifier = Modifier.alpha(alpha) // Buton grubuna da fade-in uygulayalım
        ) {
            LoginButton(
                onClick = onLoginClick,
                isLoading = state.isLoading,
                modifier = Modifier.bounceClick()
            )
            Spacer(modifier = Modifier.height(16.dp))
            GuestButton(
                onClick = onGuestClick,
                isLoading = state.isLoading,
                modifier = Modifier.bounceClick()
            )
        }
    }
}

@Composable
private fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = appLogo),
        contentDescription = stringResource(R.string.app_logo),
        // Dışarıdan gelen modifier'ı (scale, alpha) kendi boyut modifier'ımızla birleştiriyoruz.
        modifier = modifier.
        size(200.dp)
    )
}

@Composable
private fun LoginButton(
    onClick: () -> Unit,
    isLoading: Boolean, // Yükleme durumunu al
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = !isLoading, // Yükleniyorsa butonu devre dışı bırak
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .bounceClick(),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = stringResource(R.string.login_with_tmdb),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GuestButton(
    onClick: () -> Unit,
    isLoading: Boolean, // Yükleme durumunu al
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        enabled = !isLoading, // Yükleniyorsa butonu devre dışı bırak
        modifier = modifier
            .fillMaxWidth()
            .bounceClick()
    ) {
        Text(
            text = stringResource(R.string.continue_as_guest),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {

    MovieDiscoveryTheme {
        WelcomeScreen(WelcomeUiState(), {})
    }
}

