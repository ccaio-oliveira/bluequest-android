package br.com.ctech.bluequest.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.BlueQuestTheme
import br.com.ctech.bluequest.designsystem.components.BQButton
import br.com.ctech.bluequest.designsystem.components.BQButtonSize
import br.com.ctech.bluequest.designsystem.components.BQSegmentedControl
import br.com.ctech.bluequest.designsystem.components.BQTextField

@Composable
fun AuthRoute(viewModel: AuthViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AuthScreen(
        state = state,
        onModeChange = viewModel::onModeChange,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::submit
    )
}

@Composable
fun AuthScreen(
    state: AuthUiState,
    onModeChange: (AuthMode) -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val submit = {
        focusManager.clearFocus()
        onSubmit()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BQColors.bg0)
            .pointerInput(Unit) { detectTapGestures { focusManager.clearFocus() }}
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = BQSpacing.screenPadding)
            .padding(top = 56.dp, bottom = BQSpacing.sp6),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        AuthHeader(modifier = Modifier.padding(bottom = 10.dp))

        BQSegmentedControl(
            options = AuthMode.entries.map { it.actionTitle },
            selectedIndex = state.mode.ordinal,
            onSelect = { onModeChange(AuthMode.entries[it]) }
        )

        if (state.mode == AuthMode.SignUp) {
            BQTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = "Nome",
                placeholder = "Como você aparece nos rankings",
                icon = R.drawable.ic_person,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )
        }

        BQTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = "E-mail",
            placeholder = "voce@gmail.com",
            icon = R.drawable.ic_mail,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        BQTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = "Senha",
            placeholder = "********",
            icon = R.drawable.ic_lock,
            isSecure = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { submit() })
        )

        if (state.mode == AuthMode.SignIn) {
            Text(
                text = "Esqueci minha senha",
                style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold),
                color = BQColors.blueBright,
            )
        }

        state.errorMessage?.let { message ->
            Text(
                text = message,
                style = BQFont.body(BQTypeScale.caption, FontWeight.Medium),
                color = BQColors.red
            )
        }

        BQButton(
            title = state.mode.actionTitle,
            onClick = submit,
            modifier = Modifier.fillMaxWidth(),
            size = BQButtonSize.Large,
            isLoading = state.isLoading,
        )

        Text(
            text = "Recebeu um convite? Entre com sua conta e ele será aplicado automaticamente.",
            modifier = Modifier.fillMaxWidth(),
            style = BQFont.body(12.sp),
            color = BQColors.text3,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append("Blue")
                withStyle(SpanStyle(color = BQColors.blueBright)) {
                    append("Quest")
                }
            },
            style = BQFont.display(34.sp)
        )

        Text(
            text = "Desafios em grupo, pontos e ranking",
            style = BQFont.body(14.sp),
            color = BQColors.text3
        )
    }
}

@Preview
@Composable
private fun AuthScreenPreview() {
    BlueQuestTheme {
        AuthScreen(
            state = AuthUiState(mode = AuthMode.SignUp, errorMessage = "Este e-mail já está em uso."),
            onModeChange = {},
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onSubmit = {}
        )
    }
}