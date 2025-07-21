package com.mustafakocer.feature_auth.account.presentation.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafakocer.feature_auth.account.presentation.viewmodel.AccountViewModel


interface AccountNavActions {
    fun navigateToWelcome()
    fun navigateBack()
}

@Composable
fun AccountRoute(
    navActions: AccountNavActions,
     viewModel: AccountViewModel = hiltViewModel() // <-- ŞİMDİLİK YORUMDA
) {
    // Geçici, sahte state ve event handler
    val dummyState = AccountUiState(isLoggedIn = true) // veya false
    val onEvent: (AccountEvent) -> Unit = { event ->
        when (event) {
            is AccountEvent.BackClicked -> navActions.navigateBack()
            is AccountEvent.LoginClicked -> navActions.navigateToWelcome()
            is AccountEvent.LogoutClicked -> navActions.navigateToWelcome()
        }
    }

    AccountScreen(
        state = dummyState,
        onEvent = onEvent
    )

}