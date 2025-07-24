package com.mustafakocer.feature_movies.search.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.R

/**
 * A custom TopAppBar for the search screen, containing the search input field and navigation controls.
 *
 * @param searchQuery The current text to be displayed in the search field.
 * @param onQueryChange Callback invoked when the user changes the text in the search field.
 * @param onClearSearch Callback invoked when the user clicks the 'clear' (X) icon.
 * @param onNavigateBack Callback invoked when the user clicks the back navigation icon.
 * @param onSearchSubmitted Callback invoked when the user submits the search via the keyboard action.
 * @param modifier The modifier to be applied to the TopAppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onNavigateBack: () -> Unit,
    onSearchSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // UX Rationale: To provide a seamless search experience, the search field should be
    // automatically focused and the keyboard should appear as soon as the screen is displayed.
    // This is achieved using a FocusRequester and the SoftwareKeyboardController.
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester), // Attach the focus requester to this text field.
                placeholder = { Text(stringResource(R.string.search_placeholder_alt)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSearchSubmitted()
                    }),
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search)
                            )
                        }
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.bounceClick()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        modifier = modifier
    )
}