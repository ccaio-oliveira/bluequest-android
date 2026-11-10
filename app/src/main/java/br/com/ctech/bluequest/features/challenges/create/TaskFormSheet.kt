package br.com.ctech.bluequest.features.challenges.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import br.com.ctech.bluequest.designsystem.components.BQChip
import br.com.ctech.bluequest.designsystem.components.BQDateField
import br.com.ctech.bluequest.designsystem.components.BQIconButton
import br.com.ctech.bluequest.designsystem.components.BQIconButtonVariant
import br.com.ctech.bluequest.designsystem.components.BQSegmentedControl
import br.com.ctech.bluequest.designsystem.components.BQTextField
import br.com.ctech.bluequest.designsystem.components.BQTimeField
import br.com.ctech.bluequest.features.challenges.fullDateText
import java.time.LocalDate

private val weekdayTitles = listOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormSheet(
    title: String,
    draft: TaskDraft,
    errorMessage: String?,
    dateRange: ClosedRange<LocalDate>?,
    onChange: (TaskDraft) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = BQRadius.large, topEnd = BQRadius.large),
        containerColor = BQColors.bg1,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(start = BQSpacing.screenPadding, end = BQSpacing.screenPadding, bottom = BQSpacing.sp6),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = BQFont.display(16.sp, FontWeight.SemiBold)
                )

                BQBadge(
                    text = "+${draft.pointsValue} pts",
                    tone = BQBadgeTone.Points,
                    icon = R.drawable.ic_bolt_fill,
                )
            }

            BQTextField(
                value = draft.name,
                onValueChange = { onChange(draft.copy(name = it)) },
                label = "Nome",
                placeholder = "Fazer treino",
                icon = R.drawable.ic_check_circle,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            )

            BQTextField(
                value = draft.points,
                onValueChange = { value -> onChange(draft.copy(points = value.filter(Char::isDigit).take(4))) },
                label = "Pontos",
                placeholder = "5",
                icon = R.drawable.ic_bolt_fill,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            RecurrenceSection(draft = draft, dateRange = dateRange, onChange = onChange)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BQTimeField(
                    label = "Prazo",
                    time = draft.deadline,
                    onTimeChange = { onChange(draft.copy(deadline = it)) },
                    icon = R.drawable.ic_schedule,
                    modifier = Modifier.weight(1f),
                )

                Column(
                    modifier = Modifier.weight(0.8f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "Exigir foto",
                        style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold),
                        color = BQColors.text2
                    )

                    Switch(
                        checked = draft.requiresPhoto,
                        onCheckedChange = { onChange(draft.copy(requiresPhoto = it)) },
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = BQFont.body(BQTypeScale.caption, FontWeight.Medium),
                    color = BQColors.red,
                )
            }

            BQButton(
                title = "Salvar tarefa",
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                size = BQButtonSize.Large,
            )
        }
    }
}

@Composable
private fun RecurrenceSection(
    draft: TaskDraft,
    dateRange: ClosedRange<LocalDate>?,
    onChange: (TaskDraft) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(BQSpacing.sp2)) {
        Text(
            text = "Recorrência",
            style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold),
            color = BQColors.text2,
        )

        BQSegmentedControl(
            options = TaskRecurrenceMode.entries.map { it.title },
            selectedIndex = draft.mode.ordinal,
            onSelect = { onChange(draft.copy(mode = TaskRecurrenceMode.entries[it])) },
        )

        when (draft.mode) {
            TaskRecurrenceMode.FixedDays -> WeekdayChips(draft = draft, onChange = onChange)
            TaskRecurrenceMode.TimesPerWeek -> TimesPerWeekStepper(draft = draft, onChange = onChange)
            TaskRecurrenceMode.Dates -> DatesEditor(draft = draft, dateRange = dateRange, onChange = onChange)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WeekdayChips(draft: TaskDraft, onChange: (TaskDraft) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        weekdayTitles.forEachIndexed { index, title ->
            val day = index + 1
            val isSelected = day in draft.weekdays

            BQChip(
                title = title,
                isSelected = isSelected,
                onClick = {
                    onChange(draft.copy(weekdays = if (isSelected) draft.weekdays - day else draft.weekdays + day))
                },
            )
        }
    }
}

@Composable
private fun TimesPerWeekStepper(draft: TaskDraft, onChange: (TaskDraft) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(BQSpacing.sp2)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BQSpacing.sp3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BQIconButton(
                icon = R.drawable.ic_remove,
                contentDescription = "Diminuir",
                onClick = { onChange(draft.copy(timesPerWeek = draft.timesPerWeek - 1)) },
                size = 36.dp,
                enabled = draft.timesPerWeek > 1,
            )

            Text(
                text = "${draft.timesPerWeek}x por semana",
                modifier = Modifier.weight(1f),
                style = BQFont.display(20.sp, FontWeight.SemiBold),
                textAlign = TextAlign.Center,
            )

            BQIconButton(
                icon = R.drawable.ic_add,
                contentDescription = "Aumentar",
                onClick = { onChange(draft.copy(timesPerWeek = draft.timesPerWeek + 1)) },
                size = 36.dp,
                enabled = draft.timesPerWeek < 6,
            )
        }

        Text(
            text = "Em qualquer dia da semana. Se faltar pouco tempo para a meta, ela vira obrigatória.",
            style = BQFont.body(BQTypeScale.caption),
            color = BQColors.text3
        )
    }
}

@Composable
private fun DatesEditor(
    draft: TaskDraft,
    dateRange: ClosedRange<LocalDate>?,
    onChange: (TaskDraft) -> Unit,
) {
    var newDate by rememberSaveable { mutableStateOf(dateRange?.start ?: LocalDate.now()) }
    val shape = RoundedCornerShape(BQRadius.medium)

    Column(
        verticalArrangement = Arrangement.spacedBy(BQSpacing.sp3)
    ) {
        draft.dates.forEach { date ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BQColors.bg1, shape)
                    .border(1.dp, BQColors.stroke1, shape)
                    .padding(start = BQSpacing.sp3, end = BQSpacing.sp2, top = BQSpacing.sp2, bottom = BQSpacing.sp2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = fullDateText(date),
                    modifier = Modifier.weight(1f),
                    style = BQFont.body(BQTypeScale.body),
                )

                BQIconButton(
                    icon = R.drawable.ic_delete,
                    contentDescription = "Remover data",
                    onClick = { onChange(draft.copy(dates = draft.dates - date)) },
                    size = 32.dp,
                    variant = BQIconButtonVariant.Ghost
                )
            }
        }

        BQDateField(
            label = "Data",
            date = newDate,
            onDateChange = { newDate = it },
            icon = R.drawable.ic_calendar_today,
            range = dateRange,
            modifier = Modifier.fillMaxWidth()
        )

        BQButton(
            title = "Adicionar data",
            onClick = {
                if (newDate !in draft.dates) {
                    onChange(draft.copy(dates = (draft.dates + newDate).sorted()))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            icon = R.drawable.ic_add,
            variant = BQButtonVariant.Secondary
        )
    }
}