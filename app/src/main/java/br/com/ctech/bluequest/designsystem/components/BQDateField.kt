package br.com.ctech.bluequest.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.ctech.bluequest.designsystem.BQColors
import br.com.ctech.bluequest.designsystem.BQFont
import br.com.ctech.bluequest.designsystem.BQRadius
import br.com.ctech.bluequest.designsystem.BQTypeScale
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.Instant

private val dateFormat = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("pt-BR"))
private val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BQDateField(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    range: ClosedRange<LocalDate>? = null,
) {
    var isPickerOpen by rememberSaveable { mutableStateOf(false) }

    PickerField(
        label = label,
        value = date.format(dateFormat).replace(".", ""),
        icon = icon,
        onClick = { isPickerOpen = true },
        modifier = modifier,
    )

    if (isPickerOpen) {
        val selectableDates = remember(range) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = range == null || utcTimeMillis.toUtcDate() in range
            }
        }

        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            selectableDates = selectableDates,
        )

        DatePickerDialog(
            onDismissRequest = { isPickerOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { onDateChange(it.toUtcDate()) }
                        isPickerOpen = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isPickerOpen = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = state, showModeToggle = false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BQTimeField(
    label: String,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
) {
    var isPickerOpen by rememberSaveable { mutableStateOf(false) }

    PickerField(
        label = label,
        value = time.format(timeFormat),
        icon = icon,
        onClick = { isPickerOpen = true },
        modifier = modifier,
    )

    if (isPickerOpen) {
        val state = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = true,
        )

        TimePickerDialog(
            onDismissRequest = { isPickerOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeChange(LocalTime.of(state.hour, state.minute))
                        isPickerOpen = false
                    },
                ) {
                    Text("OK")
                }
            },
            title = { Text(label) },
            dismissButton = {
                TextButton(onClick = { isPickerOpen = false }) {
                    Text("Cancelar")
                }
            },
        ) {
            TimePicker(state = state)
        }
    }
}

@Composable
private fun PickerField(
    label: String,
    value: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(BQRadius.small)
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            style = BQFont.body(BQTypeScale.caption, FontWeight.SemiBold),
            color = BQColors.text2,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(shape)
                .background(BQColors.bg1)
                .border(1.dp, BQColors.stroke1, shape)
                .clickable(role = Role.Button, onClickLabel = label) {
                    focusManager.clearFocus()
                    onClick()
                }
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = BQColors.text3,
                modifier = Modifier.size(20.dp),
            )

            Text(text = value, style = BQFont.body(BQTypeScale.body), maxLines = 1)
        }
    }
}

private fun Long.toUtcDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()