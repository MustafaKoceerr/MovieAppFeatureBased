package com.mustafakocer.feature_auth.account.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_auth.R
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEvent
import com.mustafakocer.feature_auth.account.presentation.contract.AccountUiState

/**
 * A purely visual, "dumb" component that displays the user's account information or a guest view.
 *
 * @param state The current UI state to render.
 * @param onEvent A lambda to propagate user interactions up to the ViewModel.
 *
 * Architectural Note:
 * This Composable is responsible only for the presentation of the Account screen. It is driven
 * entirely by the `AccountUiState` and communicates user actions via the `onEvent` callback.
 * This makes it stateless, easy to preview, and decoupled from business logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    state: AccountUiState,
    onEvent: (AccountEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.account_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(AccountEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_desc)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            if (state.isLoggedIn) {
                LoggedInProfileCard()
                Spacer(modifier = Modifier.height(16.dp))
                LogoutButton(onLogoutClick = { onEvent(AccountEvent.LogoutClicked) })
            } else {
                GuestProfileCard(onLoginClick = { onEvent(AccountEvent.LoginClicked) })
            }
        }
    }
}

/**
 * A private helper composable that displays the view for a non-authenticated (guest) user.
 */
@Composable
private fun GuestProfileCard(onLoginClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.guest_card_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = stringResource(R.string.guest_card_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth().bounceClick()) {
            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.login_button))
        }
    }
}

/**
 * A private helper composable that displays the view for an authenticated user.
 */
@Composable
private fun LoggedInProfileCard() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text("Hello, User!", style = MaterialTheme.typography.headlineSmall)
        Text("Welcome back", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/**
 * A private helper composable for the logout button.
 */
@Composable
private fun LogoutButton(onLogoutClick: () -> Unit) {
    Button(
        onClick = onLogoutClick,
        modifier = Modifier.fillMaxWidth().bounceClick(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.logout_button))
    }
}