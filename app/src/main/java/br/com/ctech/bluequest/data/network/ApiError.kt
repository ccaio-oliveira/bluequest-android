package br.com.ctech.bluequest.data.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import retrofit2.HttpException

sealed class ApiError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class Unauthorized : ApiError("Sua sessão expirou. Entre novamente.")
    class Forbidden : ApiError("Você não tem permissão para fazer isso.")
    class TooManyRequests : ApiError("Muitas tentativas. Aguarde um minuto e tente de novo.")
    class Validation(message: String, val fields: Map<String, List<String>>) : ApiError(fields.values.firstOrNull()?.firstOrNull() ?: message)
    class DomainRule(val reason: String, val state: String?) : ApiError(messageFor(reason))
    class Server(val status: Int) : ApiError("O servidor não conseguiu responder. Tente de novo.")
    class Network(cause: Throwable) : ApiError("Sem conexão. Verifique sua internet.", cause)
    class Decoding(cause: Throwable) : ApiError("Recebemos uma resposta inesperada do servidor.", cause)

    private companion object {
        fun messageFor(reason: String): String = when (reason) {
            "user_not_participant" -> "Você não participa deste desafio."
            "occurrence_not_available" -> "Esta tarefa não está mais disponível."
            "occurrence_does_not_exist" -> "Esta tarefa não ocorre nesta data."
            "outside_challenge_period" -> "Esta data está fora do período do desafio."
            "challenge_closed" -> "Este desafio já foi encerrado."
            "start_locked" -> "O início não pode mudar depois que o desafio começou."
            "start_in_past" -> "O início não pode ser uma data passada."
            "end_in_past" -> "O término não pode ser uma data passada."
            "cannot_remove_creator" -> "O criador não pode ser removido do desafio."
            "challenge_not_started" -> "O desafio ainda não começou."
            "photo_required" -> "Esta tarefa exige uma foto."
            "weekly_target_reached" -> "Você já bateu a meta desta tarefa nesta semana."
            "current_password_invalid" -> "A senha atual não confere."
            "password_required_for_email" -> "Crie uma senha antes de trocar o e-mail."
            "invalid_reset_code" -> "Código inválido ou expirado. Confira o e-mail ou peça um novo código."
            else -> "Não foi possível concluir a ação."
        }
    }
}

suspend fun <T> apiCall(request: suspend () -> T): T = try {
    request()
} catch (e: HttpException) {
    throw e.toApiError()
} catch (e: IOException) {
    throw ApiError.Network(e)
} catch (e: SerializationException) {
    throw ApiError.Decoding(e)
}

@Serializable
private data class DomainRulePayload(val error: String, val state: String? = null)

@Serializable
private data class ValidationPayload(val message: String, val errors: Map<String, List<String>> = emptyMap())

private val errorJson = Json { ignoreUnknownKeys = true }

private fun HttpException.toApiError(): ApiError = when (code()) {
    401 -> ApiError.Unauthorized()
    403 -> ApiError.Forbidden()
    422 -> unprocessableError(response()?.errorBody()?.string().orEmpty())
    429 -> ApiError.TooManyRequests()
    else -> ApiError.Server(code())
}

private fun unprocessableError(body: String): ApiError {
    runCatching { errorJson.decodeFromString<DomainRulePayload>(body) }.getOrNull()?.let {
        return ApiError.DomainRule(it.error, it.state)
    }

    runCatching { errorJson.decodeFromString<ValidationPayload>(body) }.getOrNull()?.let {
        return ApiError.Validation(it.message, it.errors)
    }

    return ApiError.Server(422)
}