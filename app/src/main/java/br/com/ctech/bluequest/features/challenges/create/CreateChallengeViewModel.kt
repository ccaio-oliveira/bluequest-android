package br.com.ctech.bluequest.features.challenges.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ctech.bluequest.data.challenges.ChallengeRepository
import br.com.ctech.bluequest.data.network.ApiError
import br.com.ctech.bluequest.domain.models.NewChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class TaskEditor(
    val index: Int?,
    val draft: TaskDraft,
    val errorMessage: String? = null,
)

data class CreateChallengeUiState(
    val name: String = "",
    val description: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusDays(29),
    val tasks: List<TaskDraft> = emptyList(),
    val taskEditor: TaskEditor? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isCreated: Boolean = false,
) {
    val dateRange: ClosedRange<LocalDate>?
        get() = if (startDate <= endDate) startDate..endDate else null
}

@HiltViewModel
class CreateChallengeViewModel @Inject constructor(
    private val repository: ChallengeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateChallengeUiState())
    val uiState: StateFlow<CreateChallengeUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onStartDateChange(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun onEndDateChange(date: LocalDate) {
        _uiState.update { it.copy(endDate = date) }
    }

    fun onAddTask() {
        _uiState.update { it.copy(taskEditor = TaskEditor(index = null, draft = TaskDraft())) }
    }

    fun onEditTask(index: Int) {
        _uiState.update { it.copy(taskEditor = TaskEditor(index = index, draft = it.tasks[index])) }
    }

    fun onRemoveTask(index: Int) {
        _uiState.update { state ->
            state.copy(tasks = state.tasks.filterIndexed { position, _ -> position != index })
        }
    }

    fun onTaskDraftChange(draft: TaskDraft) {
        _uiState.update { it.copy(taskEditor = it.taskEditor?.copy(draft = draft, errorMessage = null)) }
    }

    fun onDismissTaskEditor() {
        _uiState.update { it.copy(taskEditor = null) }
    }

    fun onSaveTask() {
        val editor = _uiState.value.taskEditor ?: return
        val problem = editor.draft.validationError()

        if (problem != null) {
            _uiState.update { it.copy(taskEditor = editor.copy(errorMessage = problem)) }
            return
        }

        _uiState.update { state ->
            val tasks = if (editor.index != null) {
                state.tasks.mapIndexed { position, task -> if (position == editor.index) editor.draft else task }
            } else {
                state.tasks + editor.draft
            }

            state.copy(tasks = tasks, taskEditor = null)
        }
    }

    fun save() {
        val state = _uiState.value

        if (state.isSaving) return

        val problem = validationError(state)

        if (problem != null) {
            _uiState.update { it.copy(errorMessage = problem) }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                repository.createChallenge(state.toNewChallenge())
                _uiState.update { it.copy(isSaving = false, isCreated = true) }
            } catch (e: ApiError) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    private fun validationError(state: CreateChallengeUiState): String? = when {
        state.name.isBlank() -> "Dê um nome ao desafio."
        state.endDate < state.startDate -> "O término precisa ser no mesmo dia ou depois do início."
        state.tasks.isEmpty() -> "Adicione pelo menos uma tarefa."
        else -> null
    }

    private fun CreateChallengeUiState.toNewChallenge() = NewChallenge(
        name = name.trim(),
        description = description.trim().ifEmpty { null },
        startDate = startDate,
        endDate = endDate,
        timezone = ZoneId.systemDefault().id,
        tasks = tasks.map { it.toNewTask() },
    )
}