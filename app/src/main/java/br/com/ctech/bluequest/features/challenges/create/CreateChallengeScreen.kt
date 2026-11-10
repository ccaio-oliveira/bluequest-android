package br.com.ctech.bluequest.features.challenges.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.ctech.bluequest.R
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQSpacing
import br.com.ctech.bluequest.designsystem.BQTypeScale
import br.com.ctech.bluequest.designsystem.components.BQBadge
import br.com.ctech.bluequest.designsystem.components.BQBadgeTone
import br.com.ctech.bluequest.designsystem.components.BQButton
import br.com.ctech.bluequest.designsystem.components.BQButtonSize
import br.com.ctech.bluequest.designsystem.components.BQButtonVariant
import br.com.ctech.bluequest.designsystem.components.BQDateField
import br.com.ctech.bluequest.designsystem.components.BQIconButton
import br.com.ctech.bluequest.designsystem.components.BQIconButtonVariant
import br.com.ctech.bluequest.designsystem.components.BQTextField
import br.com.ctech.bluequest.features.challenges.recurrenceText
import br.com.ctech.bluequest.features.challenges.timeText
import java.time.LocalDate

@Composable
fun CreateChallengeRoute(
    onClose: () -> Unit,
    viewModel: CreateChallengeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCreated) {
        if (state.isCreated) onClose()
    }

    CreateChallengeScreen(
        state = state,
        onClose = onClose,
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onStartDateChange = viewModel::onStartDateChange,
        onEndDateChange = viewModel::onEndDateChange,
        onAddTask = viewModel::onAddTask,
        onEditTask = viewModel::onEditTask,
        onRemoveTask = viewModel::onRemoveTask,
        onSave = viewModel::save,
    )

    state.taskEditor?.let { editor ->
        TaskFormSheet(
            title = if (editor.index == null) "Nova tarefa" else "Editar tarefa",
            draft = editor.draft,
            errorMessage = editor.errorMessage,
            dateRange = state.dateRange,
            onChange = viewModel::onTaskDraftChange,
            onSave = viewModel::onSaveTask,
            onDismiss = viewModel::onDismissTaskEditor,
        )
    }
}

@Composable
fun CreateChallengeScreen(
    state: CreateChallengeUiState,
    onClose: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit,
    onAddTask: () -> Unit,
    onEditTask: (Int) -> Unit,
    onRemoveTask: (Int) -> Unit,
    onSave: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(
                start = BQSpacing.screenPadding,
                end = BQSpacing.screenPadding,
                top = BQSpacing.sp2,
                bottom = BQSpacing.sp8
            ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BQIconButton(
                icon = R.drawable.ic_close,
                contentDescription = "Fechar",
                onClick = onClose
            )

            Text(text = "Criar desafio", style = BQFont.display(BQTypeScale.title2))
        }

        BQTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = "Nome",
            placeholder = "Projeto Verão",
            icon = R.drawable.ic_flag_fill,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )

        BQTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = "Descrição",
            placeholder = "Contexto do desafio",
            icon = R.drawable.ic_notes,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BQDateField(
                label = "Início",
                date = state.startDate,
                onDateChange = onStartDateChange,
                icon = R.drawable.ic_calendar_today,
                modifier = Modifier.weight(1f),
            )

            BQDateField(
                label = "Término",
                date = state.endDate,
                onDateChange = onEndDateChange,
                icon = R.drawable.ic_event,
                modifier = Modifier.weight(1f),
            )
        }

        if (state.tasks.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(BQSpacing.sp3)) {
                state.tasks.forEachIndexed { index, task ->
                    TaskDraftRow(
                        task = task,
                        onEdit = { onEditTask(index) },
                        onRemove = { onRemoveTask(index) },
                    )
                }
            }
        }

        BQButton(
            title = "Adicionar tarefa",
            onClick = {
                focusManager.clearFocus()
                onAddTask()
            },
            modifier = Modifier.fillMaxWidth(),
            icon = R.drawable.ic_add,
            variant = BQButtonVariant.Secondary,
            enabled = !state.isSaving,
        )

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                style = BQFont.body(BQTypeScale.caption, FontWeight.Medium),
                color = BQColors.red,
            )
        }

        BQButton(
            title = "Criar desafio",
            onClick = {
                focusManager.clearFocus()
                onSave()
            },
            modifier = Modifier.fillMaxWidth(),
            size = BQButtonSize.Large,
            isLoading = state.isSaving,
        )
    }
}

@Composable
private fun TaskDraftRow(task: TaskDraft, onEdit: () -> Unit, onRemove: () -> Unit) {
    val shape = RoundedCornerShape(BQRadius.medium)
    val summary = recurrenceText(task.recurrence).replaceFirstChar { it.uppercase() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(BQColors.bg1)
            .border(1.dp, BQColors.stroke1, shape)
            .clickable(role = Role.Button, onClickLabel = "Editar tarefa", onClick = onEdit)
            .padding(start = BQSpacing.cardPadding, end = BQSpacing.sp2, top = 14.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = task.name,
                style = BQFont.body(BQTypeScale.body, FontWeight.SemiBold)
            )

            Text(
                text = "$summary · até ${timeText(task.deadline)}",
                style = BQFont.body(BQTypeScale.caption),
                color = BQColors.text3,
            )
        }

        BQBadge(
            text = "+${task.pointsValue} pts",
            tone = BQBadgeTone.Points,
            icon = R.drawable.ic_bolt_fill,
        )

        BQIconButton(
            icon = R.drawable.ic_delete,
            contentDescription = "Remover tarefa",
            onClick = onRemove,
            size = 32.dp,
            variant = BQIconButtonVariant.Ghost
        )
    }
}